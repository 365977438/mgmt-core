<form class="form-horizontal">
	<#list modelFields as field>
		<#if field.id>
	<input type="hidden" name="${idField}" id="${idField}" value="${r'${(model.'}${field.name}${r')!}'}"/>
		<#elseif field.name!="isDeleted" && field.type="java.lang.Boolean">
	<div class="form-group">
		<label class="control-label col-xs-12 col-sm-3 no-padding-right"
			for="${field.name}">${field.name}:</label>

		<div class="col-xs-12 col-sm-2">
			<div class="clearfix">
				<input type="checkbox" class="ace" name="${field.name}" id="${field.name}" ${r'<#if model??&&model.'}${field.name}>checked${r'</#if>'} disabled="disabled"/>
				<span class="lbl"></span>
			</div>
		</div>
	</div>
		<#elseif field.name!="isDeleted" && field.name!="class" && field.name!="objVersion">
		
	<div class="form-group">
		<label class="control-label col-xs-12 col-sm-3 no-padding-right"
			for="${field.name}">${field.name}:</label>

		<div class="col-xs-12 col-sm-9">
			<div class="clearfix">
				<input type="text" name="${field.name}" id="${field.name}" value="${r'${(model.'}${field.name}${r')!}'}"
					class="col-xs-12 col-sm-5" readonly="readonly"/>
			</div>
		</div>
	</div>

	<div class="space-2"></div>
		</#if>
	</#list>
	
</form>