package com.itlike.web.realm;

import com.itlike.domain.Employee;
import com.itlike.service.EmployeeService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRealm extends AuthorizingRealm {

    @Autowired
    private EmployeeService employeeService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("来到了认证-------");
        String username = (String)token.getPrincipal();
        Employee employee = employeeService.getEmployeeWithUserName(username);
        
        if (employee == null){
            return null;
        }

        System.out.println("數據庫抓到的密碼是: [" + employee.getPassword() + "]");
        System.out.println("使用的鹽值是: [" + employee.getUsername() + "]");
        
        // 這行會幫你算出在 hashIterations=2 且有鹽值的情況下，資料庫正確應該存什麼
        Md5Hash md5Hash = new Md5Hash("1234", employee.getUsername(), 2);
        System.out.println(">>> 終極答案：如果密碼是1234，資料庫密碼欄位應該填入: [" + md5Hash.toString() + "]");

        return new SimpleAuthenticationInfo(
                employee,
                employee.getPassword(),
                ByteSource.Util.bytes(employee.getUsername()),
                this.getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("授权调用-------------------");
        Employee employee = (Employee) principalCollection.getPrimaryPrincipal();
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        if(employee.getAdmin()){
            permissions.add("*:*");
        }else {
            roles = employeeService.getRolesById(employee.getId());
            permissions = employeeService.getPermissionById(employee.getId());
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
        info.addStringPermissions(permissions);
        return info;
    }
}