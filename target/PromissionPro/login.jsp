<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>用户权限管理系统</title>
    <link href="${pageContext.request.contextPath}/static/css/base.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/css/login.css" rel="stylesheet">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/plugins/easyui/jquery.min.js"></script>
    <script>
        $(function () {
            // 監聽登錄按鈕點擊事件
            $("#loginBtn").click(function () {
                $.post("${pageContext.request.contextPath}/login", $("form").serialize(), function (data) {
                   // 如果回傳的是字串，則解析為 JSON 物件
                   var res = typeof data === 'string' ? $.parseJSON(data) : data;
                   if (res.success){
                       // 跳轉到首頁
                       window.location.href = "${pageContext.request.contextPath}/index.jsp";
                   } else {
                      alert(res.msg);
                   }
                });
            });
            
            // 監聽 Enter 鍵，方便登錄
            $(document).keyup(function(event){
                if(event.keyCode == 13){
                    $("#loginBtn").trigger("click");
                }
            });
        });
    </script>
</head>
<body class="white">
<div class="login-hd">
    <div class="left-bg"></div>
    <div class="right-bg"></div>
    <div class="hd-inner">
        <span class="logo"></span>
        <span class="split"></span>
        <span class="sys-name">用户权限管理系统</span>
    </div>
</div>
<div class="login-bd">
    <div class="bd-inner">
        <div class="inner-wrap">
            <div class="lg-zone">
                <div class="lg-box">
                    <div class="lg-label"><h4>用户登录</h4></div>
                    <form>
                        <div class="lg-username input-item clearfix">
                            <i class="iconfont"></i>
                            <input type="text" value="itlike" name="username" placeholder="请输入用户名">
                        </div>
                        <div class="lg-password input-item clearfix">
                            <i class="iconfont"></i>
                            <input type="password" value="1234" name="password"  placeholder="请输入密码">
                        </div>
                        <div class="enter">
                            <a href="javascript:;" class="purchaser" id="loginBtn">点击登录</a>
                        </div>
                    </form>
                    <div class="line line-y"></div>
                    <div class="line line-g"></div>
                </div>
            </div>
            <div class="lg-poster"></div>
        </div>
    </div>
</div>
<%--<div class="login-ft">--%>
<%--    <div class="ft-inner">--%>
<%--        <div class="about-us">--%>
<%--            <a href="javascript:;">关于我们</a>--%>
<%--            <a href="http://www.itlike.com/">撩课学院</a>--%>
<%--            <a href="javascript:;">服务条款</a>--%>
<%--            <a href="javascript:;">联系方式</a>--%>
<%--        </div>--%>
<%--        <div class="address"> 课程内容版权均归 撩课教育 所有 &nbsp;编号：210019&nbsp;&nbsp;Copyright&nbsp;©&nbsp;2019&nbsp;-&nbsp;2020&nbsp;撩课&nbsp;版权所有</div>--%>
<%--        <div class="other-info">建议使用IE8及以上版本浏览器&nbsp;沪ICP備&nbsp;18036896號&nbsp;E-mail：itlike@126.com</div>--%>
<%--    </div>--%>
<%--</div>--%>
</body>
</html>