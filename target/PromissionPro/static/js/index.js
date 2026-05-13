$(function () {
    $("#tabs").tabs({
        fit:true
    })
    $('#tree').tree({
        // 修正：去掉開頭的斜線，讓請求自動帶上專案路徑 /PromissionPro/
        url:"getTreeData",
        lines:true,
        onSelect: function(node){
            var exists = $("#tabs").tabs("exists",node.text);
            if(exists){
                $("#tabs").tabs("select",node.text);
            }else {
                if (node.url !='' && node.url !=null){
                    // 修正：獲取當前專案名稱，確保 iframe 內的內容路徑正確
                    var pathName = window.location.pathname;
                    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
                    var realUrl = node.url;
                    if(realUrl.indexOf(projectName) == -1){
                        realUrl = projectName + realUrl;
                    }

                    $("#tabs").tabs("add",{
                        title:node.text,
                        content:"<iframe src='"+realUrl+"' frameborder='0' width='100%' height='100%'></iframe>",
                        closable:true
                    })
                }
            }
        },
        onLoadSuccess: function (node, data) {
            if (data && data.length > 0 && data[0].children && data[0].children.length > 0) {
                var n = $('#tree').tree('find', data[0].children[0].id);
                if(n){
                    $('#tree').tree('select', n.target);
                }
            }
        }
    });
});