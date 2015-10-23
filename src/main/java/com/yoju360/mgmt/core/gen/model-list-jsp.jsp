<script type="text/javascript">
	var ns_${modelNameUncamel} = {};

	ns_${modelNameUncamel}.viewDetail = function(id) {
		var viewDialog = new BootstrapDialog({
			cssClass: 'details-dialog',
			draggable: true,
        	closable: true,
        	closeByBackdrop: false,
            closeByKeyboard: false,
            title: '查看详情',
            message: $('<div></div>').load(sys.basePath + '/${modelNameUncamel}/show.do?id=' + id),
            buttons: [{
                label: '关闭',
                cssClass: 'btn-primary',
                action: function(dialogItself) {
                    dialogItself.close();
                }
            }]
        });
		viewDialog.open();
	};
	
	ns_${modelNameUncamel}.add = function(id) {
		sys.goInTab('${modelNameUncamel}_index', sys.basePath + '/${modelNameUncamel}/edit.do', '添加${modelDesc}');
	};

	ns_${modelNameUncamel}.edit = function(id) {
		sys.goInTab('${modelNameUncamel}_index', sys.basePath + '/${modelNameUncamel}/edit.do?id=' + id, '编辑${modelDesc}');
	};
	
	ns_${modelNameUncamel}.del = function(id) {
		var delConfirmDialog = new BootstrapDialog({
        	closable: true,
        	title: '警告',
            message: '确定删除此数据？',
            buttons: 
            	[
				  {
				    label: '确定',
				    cssClass: 'btn-danger',
				    action: function(dialogRef) {
				    	$.ajax({
				    		url: sys.basePath + "/${modelNameUncamel}/del.do",
							type: "POST",
							dataType: "json",
        					data: {"array": JSON.stringify([id])},
							success:function(response, st, xhr) {
								if(typeof(response.success)=="undefined"){
									console.erro("无结果返回/登录超时或未登录");
									sys.alertInfo("无结果返回/登录超时或未登录", "warning");
									return;
								}
								if (response.success == true) {
									dialogRef.close();
									sys.alertInfo(response.msg, "success");
									ns_${modelNameUncamel}.oTable.ajax.reload();
								}else {
									console.error(response.msg);
									sys.alertInfo(response.msg, "warning");
								}
							},
							error:function(xhr,type,msg){
								console.error(msg);
								sys.alertInfo(response.msg, "danger");
							}
				    	});
				    }
				 },
            	 {
            		label:'取消',
            		cssClass: 'btn-default',
            		action: function(dialogRef) {
            			dialogRef.close();
            		}
            	 }
	           ]
        });
		delConfirmDialog.open();
	};
	
	ns_${modelNameUncamel}.delSelected = function() {
		var idArray = [];
		var table = $('#${modelNameUncamel}-table').DataTable();
		for (var i=0; i<table.rows('.success').data().length;i++) {
			idArray.push(table.rows('.success').data()[i]['${idField}']);
		}
		
		if(idArray.length == 0) {
			sys.alertInfo("请选择数据！","info");
			return false;
		}
		
		var delConfirmDialog = new BootstrapDialog({
        	closable: true,
        	title: '警告',
            message: '确定删除所选数据？',
            buttons: 
            	[
				  {
				    label: '确定',
				    cssClass: 'btn-danger',
				    action: function(dialogRef) {
				    	
							$.ajax({
								url: sys.basePath + "/${modelNameUncamel}/del.do",
								type: "POST",
								dataType: "json",
								data: {"array": JSON.stringify(idArray)},
								success:function(response,st, xhr) {
									if(typeof(response.success)=="undefined"){
										console.erro("无结果返回/登录超时或未登录");
										sys.alertInfo("无结果返回/登录超时或未登录", "warning");
										return;
									}
									if (response.success == true) {
										dialogRef.close();
										sys.alertInfo(response.msg, "warning");
										ns_${modelNameUncamel}.oTable.ajax.reload();
									}else {
										console.error(response.msg);
									}
								},
								error:function(xhr,type,msg){
									console.error(msg);
									sys.alertInfo(msg, "danger");
								}
							});
						}
				 },
            	 {
            		label:'取消',
            		cssClass: 'btn-default',
            		action: function(dialogRef) {
            			dialogRef.close();
            		}
            	 }
	           ]
        });
		delConfirmDialog.open();
	};
	
	jQuery(function($) {
		//initiate dataTables plugin
		ns_${modelNameUncamel}.oTable = 
		$('#${modelNameUncamel}-table')
		//.wrap("<div class='dataTables_borderWrap' />")   //if you are applying horizontal scrolling (sScrollX)
		.DataTable( {
			serverSide: true,
			ajax: sys.basePath + '/${modelNameUncamel}/list.do',
			columns: [
						{"name": "id", "data": "${idField}", render: function ( data, type, row ) {
						    if ( type === 'display' ) {
						        return "<label class=\"pos-rel\"> <input\
						        type=\"checkbox\" class=\"ace\" /> <span class=\"lbl\"></span>\
						        </label>";
						    }
						    return data;
						}},
						 {"name": "operation", "data": "${idField}", render: function ( data, type, row ) {
							    if ( type === 'display' ) {
							        var html= '<div class="hidden-sm hidden-xs action-buttons">\
												<a class="blue" title="查看" href="javascript:ns_${modelNameUncamel}.viewDetail(' + data + ');">\
											<i class="ace-icon fa fa-search-plus bigger-130"></i>\
										</a>\
										<a class="green" title="编辑" href="javascript:ns_${modelNameUncamel}.edit(' + data + ');">\
											<i class="ace-icon fa fa-pencil bigger-130"></i>\
										</a>\
										<a class="red" title="删除" href="javascript:ns_${modelNameUncamel}.del(' + data + ');">\
											<i class="ace-icon fa fa-trash-o bigger-130"></i>\
										</a>\
									</div>\
									<div class="hidden-md hidden-lg">\
										<div class="inline pos-rel">\
											<ul class="dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close">\
												<li>\
													<a title="查看" href="javascript:ns_${modelNameUncamel}.viewDetail(' + data + ');" class="tooltip-info" data-rel="tooltip" title="View">\
														<span class="blue">\
															<i class="ace-icon fa search-plus bigger-120"></i>\
														</span>\
													</a>\
												</li>\
												<li>\
													<a title="编辑" href="javascript:ns_${modelNameUncamel}.edit(' + data + '); class="tooltip-success" data-rel="tooltip" title="Edit">\
														<span class="green">\
															<i class="ace-icon fa fa-pencil-square-o bigger-120"></i>\
														</span>\
													</a>\
												</li>\
												<li>\
													<a title="删除" href="javascript:ns_${modelNameUncamel}.del(' + data + ');" class="tooltip-error" data-rel="tooltip" title="Delete">\
														<span class="red">\
															<i class="ace-icon fa fa-trash-o bigger-120"></i>\
														</span>\
													</a>\
												</li>\
											</ul>\
										</div>\
									</div>';
									return html;
							    }
							    return data;
							}},
						<#list modelFields as field>
						<#if !field.id && field.name!="class" && field.name!="objVersion"> 
						{"name": "${field.name}", "data": "${field.name}"<#if field.type=="java.util.Date">, "type": "date"</#if> },
						</#if>
						</#list>
			        ],
			bAutoWidth: false,
	          "aLengthMenu": [
	          [1, 10, 50, 100,500, -1],
	          [1, 10, 50, 100,500, "All"]
	          ],
	          "iDisplayLength" : 10,
			"aaSorting": [],
			"columnDefs": [ {
			      "targets": 0,
			      "searchable": false,
			      "sortable": false
			    },{
				      "targets": 1,
				      "searchable": false,
				      "sortable": false
				},{
			      "targets": 4,
			      "searchable": false,
			      "sortable": false
			    } ],
			language: {
				"url": "./assets/js/jquery-datatable-zh-CN.json"
			},
			"order": [[ 0, "desc" ]]
	    });
		
		sys.addDatatableTools(2, '#${modelNameUncamel}_tableTools-container', 'ns_${modelNameUncamel}.exportExcel()', '#${modelNameUncamel}-table', ns_${modelNameUncamel}.oTable);
	});
	
	ns_${modelNameUncamel}.exportExcel = function() {
		sys.exportExcel('#${modelNameUncamel}-table', sys.basePath + '/${modelNameUncamel}/exportExcel.do', '${modelNameUncamel}.xls');
	};
</script>

<!-- div.dataTables_borderWrap -->
<div class="clearfix">
	<div class="pull-left">
       	<button class="btn btn-sm btn-info" onclick="javascript:ns_${modelNameUncamel}.add();">
       		<i class="fa fa-plus-square"></i>&nbsp;&nbsp;添加</button>
		<button class="delete btn btn-sm btn-danger" onclick="javascript:ns_${modelNameUncamel}.delSelected();">
			<i class="fa fa-trash"></i>&nbsp;&nbsp;删除</button>
  	</div>
	<div class="pull-right tableTools-container" id="${modelNameUncamel}_tableTools-container">
	</div>
</div>
<div>
	<table id="${modelNameUncamel}-table"
		class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th><label class="pos-rel"> <input
						type="checkbox" class="ace" /> <span class="lbl"></span>
				</label></th>
				<#list modelFields as field>
				<#if !field.id && field.name!="class"> 
				<th>${field.name}</th>
				</#if>
				</#list>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>
<!-- // div.dataTables_borderWrap -->
