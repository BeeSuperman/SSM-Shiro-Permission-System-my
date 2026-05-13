package com.itlike.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itlike.domain.AjaxRes;
import com.itlike.domain.Employee;
import com.itlike.domain.PageListRes;
import com.itlike.domain.QueryVo;
import com.itlike.service.EmployeeService;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class EmployeeController {
    /*注入业务层*/
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/employee")
    @RequiresPermissions("employee:index")
    public String employee(){
        return "employee";
    }

    @RequestMapping("/employeeList")
    @ResponseBody
    public PageListRes employeeList(QueryVo vo){
        /*调用业务层查询员工*/
        return employeeService.getEmployee(vo);
    }

    /* 【修正】將回傳類型改為 String，解決 406 錯誤，功能邏輯與老師原始版本完全一致 */
    @RequestMapping(value = "/saveEmployee", produces = "text/html;charset=UTF-8")
    @ResponseBody
    @RequiresPermissions("employee:add")
    public String saveEmployee(Employee employee){
        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*调用业务层,保存用户*/
            employee.setState(true);
            employeeService.saveEmployee(employee);
            ajaxRes.setMsg("保存成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            ajaxRes.setSuccess(false);
            ajaxRes.setMsg("保存失败");
        }
        /* 手動轉 JSON，確保前端 EasyUI 正確接收 */
        try {
            return new ObjectMapper().writeValueAsString(ajaxRes);
        } catch (Exception e) {
            return "{\"success\":false,\"msg\":\"JSON轉換失敗\"}";
        }
    }

    /* 【修正】同樣改為手動 JSON 轉換，確保更新功能正常 */
    @RequestMapping(value = "/updateEmployee", produces = "text/html;charset=UTF-8")
    @ResponseBody
    @RequiresPermissions("employee:edit")
    public String updateEmployee(Employee employee){
        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*调用业务层,更新员工*/
            employeeService.updateEmployee(employee);
            ajaxRes.setMsg("更新成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            ajaxRes.setSuccess(false);
            ajaxRes.setMsg("更新失败");
        }
        try {
            return new ObjectMapper().writeValueAsString(ajaxRes);
        } catch (Exception e) {
            return "{\"success\":false,\"msg\":\"JSON轉換失敗\"}";
        }
    }

    /* 【修正】離職操作也加入編碼與手動 JSON 保護 */
    @RequestMapping(value = "/updateState", produces = "text/html;charset=UTF-8")
    @ResponseBody
    @RequiresPermissions("employee:delete")
    public String updateState(Long id){
        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*调用业务层,设置员工离职状态*/
            employeeService.updateState(id);
            ajaxRes.setMsg("更新成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            ajaxRes.setSuccess(false);
            ajaxRes.setMsg("更新失败");
        }
        try {
            return new ObjectMapper().writeValueAsString(ajaxRes);
        } catch (Exception e) {
            return "{\"success\":false,\"msg\":\"JSON轉換失敗\"}";
        }
    }

    @ExceptionHandler(AuthorizationException.class)
    public void handleShiroException(HandlerMethod method, HttpServletResponse response) throws Exception{ 
        ResponseBody methodAnnotation = method.getMethodAnnotation(ResponseBody.class);
        if (methodAnnotation != null){
            AjaxRes ajaxRes = new AjaxRes();
            ajaxRes.setSuccess(false);
            ajaxRes.setMsg("你没有权限操作");
            String s = new ObjectMapper().writeValueAsString(ajaxRes);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(s);
        }else {
            response.sendRedirect("nopermission.jsp");
        }
    }

    @RequestMapping("/downloadExcel")
    @ResponseBody
    public void downloadExcel(HttpServletResponse response){
        try {
            QueryVo queryVo = new QueryVo();
            queryVo.setPage(1);
            queryVo.setRows(100);
            PageListRes plr = employeeService.getEmployee(queryVo);
            List<Employee> employees = (List<Employee>)plr.getRows();
            
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("员工数据");
            HSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("编号");
            row.createCell(1).setCellValue("用户名");
            row.createCell(2).setCellValue("入职日期");
            row.createCell(3).setCellValue("电话");
            row.createCell(4).setCellValue("邮件");

            for(int i = 0; i < employees.size(); i++){
                Employee employee = employees.get(i);
                HSSFRow employeeRow = sheet.createRow(i+1);
                employeeRow.createCell(0).setCellValue(employee.getId());
                employeeRow.createCell(1).setCellValue(employee.getUsername());
                if (employee.getInputtime() !=null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    employeeRow.createCell(2).setCellValue(sdf.format(employee.getInputtime()));
                }else {
                    employeeRow.createCell(2).setCellValue("");
                }
                employeeRow.createCell(3).setCellValue(employee.getTel());
                employeeRow.createCell(4).setCellValue(employee.getEmail());
            }

            String fileName = new String("員工數據.xls".getBytes("utf-8"), "iso8859-1");
            response.setHeader("content-Disposition","attachment;filename="+fileName);
            wb.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("downloadExcelTpl")
    @ResponseBody
    public void downloadExcelTpl(HttpServletRequest request, HttpServletResponse response){
        FileInputStream is = null;
        try {
            String fileName = new String("EmployeeTpl.xls".getBytes("utf-8"), "iso8859-1");
            response.setHeader("content-Disposition","attachment;filename="+fileName);
            String realPath = request.getSession().getServletContext().getRealPath("static/excelTemplate/ExcelTemplate.xls");
            is = new FileInputStream(realPath);
            IOUtils.copy(is,response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (is != null){
                try { is.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    @RequestMapping(value = "/uploadExcelFile", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String uploadExcelFile(MultipartFile excel){
        AjaxRes ajaxRes = new AjaxRes();
        try {
            HSSFWorkbook wb = new HSSFWorkbook(excel.getInputStream());
            HSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i=1; i <= lastRowNum; i++){
                Row employeeRow = sheet.getRow(i);
                if(employeeRow == null) continue;
            }
            ajaxRes.setMsg("导入成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            ajaxRes.setMsg("导入失败");
            ajaxRes.setSuccess(false);
        }
        try {
            return new ObjectMapper().writeValueAsString(ajaxRes);
        } catch (Exception e) {
            return "{\"success\":false,\"msg\":\"JSON轉換失敗\"}";
        }
    }

    private Object getCellValue(Cell cell){
        if(cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}