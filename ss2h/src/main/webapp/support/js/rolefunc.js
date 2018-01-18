$(function () {
    layui.use('element', function () {
        var element = layui.element;
    });
    layui.use('form', function () {
    	var rol_id="";
    	var funcidlist="";
        var form =layui.form;
        //增加
        form.on('submit(btn)', function (data) {
        	$("input:radio[name='roleid']:checked").each(function(){//遍历name=funcid的单选按钮radio
        		rol_id=$(this).val();
        		});
        	$("input:checkbox[name='funcid']:checked").each(function(){//遍历name=funcid的多选框checkbox
        		funcidlist=funcidlist+","+$(this).val();
        		});
        	  $.ajax({
                  url: ctx + "/rolfc/rolfc_add",
                  type: "POST",
                  data:{
        		    'tRoleFuncEntity.roleid':rol_id,
        		    'tRoleFuncEntity.funcId':funcidlist
        	       },
                  dataType: "json",
                  beforeSend: function (xhr) {
                      $("#btn").attr('disabled', "true");
                  },
                  success: function (data) {
                      var error = parseInt(data.error);
                      if (error === 0) {
                          $("#btn").next().click();
                      }
                      layer.msg(data.message);
                      getfunc(rol_id);
                  },
                  error: function () {
                      layer.msg("连接服务器失败");
                  },
                  complete: function () {
                      $("#btn").removeAttr("disabled");
                  }
              });
        
            return false;
        });
    });
   
    function getfunc(data){
    	 $("#funclist").html("");
    	  $.ajax({
			   url:ctx + "/rolfc/rolfc_findnofuncbyrole?role="+data,
			   type:"POST",
			   dataType:"json",
			   success:function(data){
				$.each(data.tfunclist,function(i,item){
					var checkbox='<input type="checkbox" name="funcid" value="'+item.fId+'" title="'+item.fName+'">';
   				$("#funclist").append(checkbox);
   				layui.use('form',function(){
   			    	 var form = layui.form;
   			         form.render();
   				});
     		
				});
				},
				error:function(data){
					layer.msg(data.message);
				}
			   
		   });
    }
    layui.use('form',function(){
    	var form = layui.form;
    	form.on('radio(radio1)', function(data){
    		  getfunc(data.value);
    		  return false;
    		});  
    });
    layui.use('form', function () {
        var form = layui.form;
        //监听角色
        form.on('select(searchChange)', function (data) {
            var role = data.value;
            init(role);
            return false;
        });
    });
    function init(role) {
    
        layui.use('table', function () {
            var table = layui.table;
            var tableOptions = {
                url: '${ctx}/rolfc/rolfc_findAll',
                method: 'POST',
                id: 'listReload',
                where: {
                    'role': role
                },
                page: true,
                request: {pageName: 'current', limitName: 'currentTotal'},
                response: {
                    statusName: 'error',
                    statusCode: 0,
                    msgName: 'message',
                    countName: 'total',
                    dataName: 'rolefunclist'
                }
            };
            table.init('table', tableOptions);
            //监听工具条
            table.on('tool(table)', function (obj) {
                var data = obj.data;
                if (obj.event === 'detail') {
                    layer.msg('ID：' + data.id + ' 的查看操作');
                } else if (obj.event === 'del') {
                    layer.confirm('真的删除吗', function (index) {
                        $.ajax({
                            url: ctx + "/rolfc/rolfc_delete",
                            type: "POST",
                            data: {
                            	 'tRoleFuncEntity.roleid':data.role_id,
                            	 'tRoleFuncEntity.orderNum':data.order,
                     		     'tRoleFuncEntity.funcId':data.func_id
                            },
                            dataType: "json",
                            success: function (data) {
                                var error = parseInt(data.error);
                                if (error === 0) {
                                    obj.del();
                                }
                                layer.alert(data.message);
                            },
                            error: function () {
                                layer.alert("连接服务器失败");
                            },
                            complete: function () {
                                layer.close(index);
                            }
                        });
                    });
                }
            });
        });
    }

    //打开页面时加载一次数据
    init($("#role").val());
    getfunc("1");
});