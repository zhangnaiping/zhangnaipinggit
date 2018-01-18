layui.use(['element', 'layer'], function () {
    var element = layui.element;
    var layer = layui.layer;
    $(function () {
        $("#urls").find("a").each(function (i) {
            if (i === 0) {
                $(this).click();
                $("#show_page").attr("src", $(this).prop("href"));
            }
        });
        $("#logon").click(function () {
            $.ajax({
                url: ctx + "/user/user_logon",
                type: "POST",
                data: {
                    'userEntity.userid': 1
                },
                dataType: "json",
                success: function (data) {
                    var error = parseInt(data.error);
                    if (error === 0) {
                        layer.open({
                            content: data.message + "，即将关闭该页面。"
                            , btn: ['确定']
                            , yes: function (index, layero) {
                                layer.closeAll(index);
                                window.location.href = ctx + "/user/userManage";
                            }
                            , cancel: function () {
                                return false;
                            }
                        });
                    } else {
                        layer.msg(data.message);
                    }
                },
                error: function () {
                    layer.msg("连接服务器失败");
                }
            });
        });
    });
});