package com.itlike.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itlike.domain.*;
import com.itlike.mapper.MenuMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public PageListRes getMenuList(QueryVo vo) {
        /*调用mapper 查询菜单 */
        Page<Object> page = PageHelper.startPage(vo.getPage(), vo.getRows());
        List<Menu> menus = menuMapper.selectAll();
        /*封装成pageList*/
        PageListRes pageListRes = new PageListRes();
        pageListRes.setTotal(page.getTotal());
        pageListRes.setRows(menus);
        return pageListRes;
    }

    @Override
    public List<Menu> parentList() {
        return menuMapper.selectAll();
    }

    @Override
    public void saveMenu(Menu menu) {
        menuMapper.saveMenu(menu);
    }

    @Override
    public AjaxRes updateMenu(Menu menu) {
        AjaxRes ajaxRes = new AjaxRes();
        /* 修正：加入 null 判斷防止空指標 */
        if (menu.getParent() == null || menu.getParent().getId() == null) {
             menuMapper.updateByPrimaryKey(menu);
             ajaxRes.setMsg("保存成功");
             ajaxRes.setSuccess(true);
             return ajaxRes;
        }
        
        Long id = menu.getParent().getId();
        Long parent_id = menuMapper.selectParentId(id);
        if (menu.getId().equals(parent_id)){
            ajaxRes.setMsg("不能设置自己的子菜单为父菜单");
            ajaxRes.setSuccess(false);
            return ajaxRes;
        }
        /*更新*/
        try {
            menuMapper.updateByPrimaryKey(menu);
            ajaxRes.setMsg("保存成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setSuccess(false);
            ajaxRes.setMsg("保存失败");
            System.out.println(e);
        }
        return ajaxRes;
    }

    @Override
    public AjaxRes deleteMenu(Long id) {
        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*1.打破菜单关系*/
            menuMapper.updateMenuRel(id);
            /*2.删除记录*/
            menuMapper.deleteByPrimaryKey(id);
            ajaxRes.setMsg("删除成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setSuccess(false);
            ajaxRes.setMsg("删除失败");
            System.out.println(e);
        }
        return ajaxRes;
    }

    @Override
    public List<Menu> getTreeData() {
        List<Menu> treeData = menuMapper.getTreeData();
        Subject subject = SecurityUtils.getSubject();
        Employee employee = (Employee)subject.getPrincipal();
        
        /* 【修正】增加安全檢查，如果用戶沒登入，回傳空清單避免後續報錯 */
        if (employee == null) {
            return new ArrayList<>();
        }

        /* 如果是管理員，直接返回全部樹，不需要校驗權限 */
        if (employee.getAdmin()){
            return treeData;
        } else {
            /* 做检验权限 */
            checkPermission(treeData);
        }
        return treeData;
    }

    public void checkPermission(List<Menu> menus){
        Subject subject = SecurityUtils.getSubject();
        Iterator<Menu> iterator = menus.iterator();
        while (iterator.hasNext()){
            Menu menu = iterator.next();
            if (menu.getPermission() != null){
                String presource = menu.getPermission().getPresource();
                /* 如果沒有對應權限，從集合移除 */
                if (!subject.isPermitted(presource)){
                    iterator.remove();
                    continue;
                }
            }
            /* 【核心修正】加入空值判斷！原本崩潰的第 100 行位置 */
            if (menu.getChildren() != null && menu.getChildren().size() > 0){
                checkPermission(menu.getChildren());
            }
        }
    }
}