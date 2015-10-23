<script src="./assets/js/jquery.validate.js"></script>
<script src="./assets/js/jquery.validate-zh-CN.js"></script>

${r'<#import "/spring.ftl" as spring />'}

<script type="text/javascript">
jQuery(function($) {
	$('#${modelNameUncamel}-edit-form').validate({
		errorElement: 'div',
		errorClass: 'help-block',
		focusInvalid: false,
		ignore: "",
		rules: {
			
		},
	
		messages: {
			
		},
	
		highlight: function (e) {
			$(e).closest('.form-group').removeClass('has-info').addClass('has-error');
		},
	
		success: function (e) {
			$(e).closest('.form-group').removeClass('has-error');//.addClass('has-info');
			$(e).remove();
		}
	});
});

var ${modelNameUncamel}_edit = {};

${modelNameUncamel}_edit.save = function() {
	if (!$('#${modelNameUncamel}-edit-form').valid()) {
		return;
	}
	var obj = sys.form2Object('#${modelNameUncamel}-edit-form');
	$.ajax({
		url: $('#${modelNameUncamel}-edit-form').attr('action'),
		type: "POST",
		dataType: "json",
		data: {"object": JSON.stringify(obj)},
		success:function(response,st, xhr) {
			if(typeof(response.success)=="undefined"){
				sys.alertInfo("无结果返回/登录超时或未登录", 'danger');
				return;
			}
			if (response.success == true) {
				sys.alertInfo(response.msg, 'success');
				sys.backInTab('${modelNameUncamel}_index');
			}else {
				console.error(response.msg);
				sys.alertInfo(response.msg, 'danger');
			}
		},
		error:function(xhr,type,msg){
			console.error(msg);
			sys.alertInfo(msg, 'danger');
		}
	});
};

${modelNameUncamel}_edit.back = function() {
	sys.backInTab('${modelNameUncamel}_index');
};
</script>
<form class="form-horizontal" id="${modelNameUncamel}-edit-form" method="post" action="${r'${ctx.getContextPath()}'}/${modelNameUncamel}/save.do">
	<#list modelFields as field>
		<#if field.id>
	<input type="hidden" name="${idField}" id="${modelNameUncamel}_${idField}" value="${r'${(model.'}${field.name}${r')!}'}"/>
		<#elseif field.name!="isDeleted" && field.type="java.lang.Boolean">
	<div class="form-group">
		<label class="control-label col-xs-12 col-sm-3 no-padding-right"
			for="${modelNameUncamel}_${field.name}">${field.name}:</label>

		<div class="col-xs-12 col-sm-2">
			<div class="clearfix">
				<input type="checkbox" class="ace" name="${field.name}" id="${modelNameUncamel}_${field.name}" ${r'<#if model??&&model.'}${field.name}>checked${r'</#if>'}/>
				<span class="lbl"></span>
			</div>
		</div>
	</div>
		<#elseif field.name!="isDeleted" && field.name!="class" && field.name!="objVersion">
		
	<div class="form-group">
		<label class="control-label col-xs-12 col-sm-3 no-padding-right"
			for="${modelNameUncamel}_${field.name}">${field.name}:</label>

		<div class="col-xs-12 col-sm-9">
			<div class="clearfix">
				<input type="text" name="${field.name}" id="${modelNameUncamel}_${field.name}" value="${r'${(model.'}${field.name}${r')!}'}"
					class="col-xs-12 col-sm-5"/>
			</div>
		</div>
	</div>

	<div class="space-2"></div>
		</#if>
	</#list>
	<div class="form-group">
		<div class="col-xs-12 col-sm-9 center">
			<div class="clearfix">
				<button type="button" class="btn btn-info" onclick="javascript:${modelNameUncamel}_edit.save();">
   					<i class="ace-icon fa fa-floppy-o"></i>保存
 				</button>
 				<button type="button" class="btn btn-default" onclick="javascript:${modelNameUncamel}_edit.back();">
   					<i class="ace-icon fa fa-times red2"></i>取消
 				</button>
			</div>
		</div>
	</div>
</form>