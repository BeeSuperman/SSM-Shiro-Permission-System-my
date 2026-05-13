$(function () {
    /*员式数据列表*/
    $("#dg").datagrid({
        url:"employeeList", 
        columns:[[
            {field:'username',title:'姓名',width:100,align:'center'},
            {field:'inputtime',title:'入职时间',width:100,align:'center'},
            {field:'tel',title:'电话',width:100,align:'center'},
            {field:'email',title:'邮箱',width:100,align:'center'},
            {field:'department',title:'部门',width:100,align:'center',formatter: function(value,row,index){
                if (value){
                    return value.name;
                }
            }},
            {field:'state',title:'状态',width:100,align:'center',formatter: function(value,row,index){
                    if(row.state){
                        return "在职";
                    }else {
                        return "<font style='color: red'>离职</font>"
                    }
                }},
            {field:'admin',title:'管理员',width:100,align:'center',formatter: function(value,row,index){
                    if(row.admin){
                        return "是";
                    }else {
                        return "否"
                    }
                }},
        ]],
        fit:true,
        fitColumns:true,
        rownumbers:true,
        pagination:true,
        singleSelect:true,
        striped:true,
        toolbar:"#tb",
        onClickRow:function (rowIndex,rowData) {
            /*判断当前行是否是离职状态*/
            if(!rowData.state){
                /*离职,把离职按钮禁用*/
                $("#delete").linkbutton("disable");
            }else {
                /*离职,把离职按钮启用*/
                $("#delete").linkbutton("enable");
            }
        }
    });

    /*对话框*/
    $("#dialog").dialog({
        width:350,
        height:400,
        closed:true,
        buttons:[{
            text:'保存',
            handler:function(){
                var id = $("[name='id']").val();
                var url;
                if(id){
                    url = "updateEmployee";
                }else {
                    url= "saveEmployee";
                }

                $("#employeeForm").form("submit",{
                    url:url,
                    onSubmit:function(param){
                       var values =  $("#role").combobox("getValues");
                       for(var i = 0; i < values.length; i++){
                           var rid  =  values[i];
                           param["roles["+i+"].rid"] = rid;
                       }
                    },
                    success:function (data) {
                      // 【修正】確保將字串解析為 JSON 物件
                      data = $.parseJSON(data);
                      if (data.success){
                          $.messager.alert("温馨提示",data.msg);
                          $("#dialog").dialog("close");
                          $("#dg").datagrid("reload");
                      } else {
                          $.messager.alert("温馨提示",data.msg);
                      }
                    }
                });
            }
        },{
            text:'关闭',
            handler:function(){
                $("#dialog").dialog("close");
            }
        }]
    });

    /*监听添加按钮点击*/
    $("#add").click(function () {
        $("#dialog").dialog("setTitle","添加员工");
        $("#password").show();
        $("#employeeForm").form("clear");
        $("[name='password']").validatebox({required:true});
        $("#dialog").dialog("open");
    });

    /*监听编辑按钮点击*/
    $("#edit").click(function () {
        var rowData = $("#dg").datagrid("getSelected");
        if(!rowData){
            $.messager.alert("提示","选择一行数据进行编辑");
            return;
        }
        $("[name='password']").validatebox({required:false});
        $("#password").hide();
        $("#dialog").dialog("setTitle","编辑员工");
        $("#dialog").dialog("open");
        rowData["department.id"] = rowData["department"].id;
        rowData["admin"] = rowData["admin"]+"";
        
        $.get("getRoleByEid?id="+rowData.id,function (data) {
            $("#role").combobox("setValues",data);
        });

        $("#employeeForm").form("load",rowData);
    });

    /*部门选择 下拉列表*/
    $("#department").combobox({
        width:150,
        panelHeight:'auto',
        editable:false,
        url:'departList', 
        textField:'name',
        valueField:'id'
    });

    /*是否为管理员选择*/
    $("#state").combobox({
        width:150,
        panelHeight:'auto',
        textField:'label',
        valueField:'value',
        editable:false,
        data:[{label:'是',value:'true'},{label:'否',value:'false'}]
    });

    /*选择角色下拉列表*/
    $("#role").combobox({
        width:150,
        panelHeight:'auto',
        editable:false,
        url:'roleList',
        textField:'rname',
        valueField:'rid',
        multiple:true
    })

    /*设置离职按钮点击*/
    $("#delete").click(function () {
        var rowData = $("#dg").datagrid("getSelected");
        if(!rowData){
            $.messager.alert("提示","选择一行数据进行操作");
            return;
        }
        $.messager.confirm("确认","是否做离职操作",function (res) {
           if(res){
               $.get("updateState?id="+rowData.id,function (data) {
                   // 【關鍵修正】將回傳的字串解析為 JSON，解決 undefined 問題
                   var resObj = $.parseJSON(data);
                   if (resObj.success){
                       $.messager.alert("温馨提示", resObj.msg);
                       $("#dg").datagrid("reload");
                   } else {
                       $.messager.alert("温馨提示", resObj.msg);
                   }
               });
           }
        });
    });

    /*监听搜索按钮点击*/
    $("#searchbtn").click(function () {
       var keyword =  $("[name='keyword']").val();
        $("#dg").datagrid("load",{keyword:keyword});
    });

    /*监听刷新点击*/
    $("#reload").click(function () {
        $("[name='keyword']").val('');
        $("#dg").datagrid("load",{});
    });

    $("#excelOut").click(function () {
       window.open('downloadExcel');
    });

    $("#excelUpload").dialog({
        width:260,
        height:180,
        title:"导入Excel",
        buttons:[{
            text:'保存',
            handler:function(){
                $("#uploadForm").form("submit",{
                    url:"uploadExcelFile",
                    success:function (data) {
                        data = $.parseJSON(data);
                        if (data.success){
                            $.messager.alert("温馨提示",data.msg);
                            $("#excelUpload").dialog("close");
                            $("#dg").datagrid("reload");
                        } else {
                            $.messager.alert("温馨提示",data.msg);
                        }
                    }
                })
            }
        },{
            text:'关闭',
            handler:function(){
                $("#excelUpload").dialog("close");
            }
        }],
        closed:true
    })

    $("#excelImpot").click(function () {
        $("#excelUpload").dialog("open");
    });

    $("#excelIn").click(function () {
        $("#excelUpload").dialog("open");
    });

    /*下载Excel模板*/
    $("#downloadTml").click(function () {
        window.open('downloadExcelTpl');
    });
});