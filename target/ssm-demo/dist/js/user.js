$(function () {
    //隐藏错误提示框
    $('.add-error-info').css("display", "none");
    $('.edit-error-info').css("display", "none");

    $("#jqGrid").jqGrid({
        url: 'users/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, hidden: true, key: true},
            {label: '登录名', name: 'userName', index: 'userName', sortable: false, width: 80},
            {label: '添加时间', name: 'createTime', index: 'createTime', sortable: false, width: 80}
        ],
        height: 485,
        rowNum: 10,
        rowList: [10, 30, 50],
        styleUI: 'Bootstrap',
        loadtext: '信息读取中...',
        rownumbers: true,
        rownumWidth: 35,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "data.list",
            page: "data.currPage",
            total: "data.totalPage",
            records: "data.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    //importV1
    new AjaxUpload('#importV1Button', {
        action: 'users/importV1',
        name: 'file',
        autoSubmit: true,
        responseType: "json",
        onSubmit: function (file, extension) {
            if (!(extension && /^(xlsx)$/.test(extension.toLowerCase()))) {
                alert('只支持xlsx格式的文件！', {
                    icon: "error",
                });
                return false;
            }
        },
        onComplete: function (file, r) {
            if (r.resultCode == 200) {
                alert("成功导入" + r.data + "条记录！");
                reload();
                return false;
            } else {
                alert(r.message);
            }
        }
    });

    //importV2
    new AjaxUpload('#uploadExcelV2', {
        action: 'upload/file',
        name: 'file',
        autoSubmit: true,
        responseType: "json",
        onSubmit: function (file, extension) {
            if (!(extension && /^(xlsx)$/.test(extension.toLowerCase()))) {
                alert('只支持xlsx格式的文件！', {
                    icon: "error",
                });
                return false;
            }
        },
        onComplete: function (file, r) {
            if (r.resultCode == 200) {
                console.log(r);
                $("#fileUrl").val(r.data);
                return false;
            } else {
                alert(r.message);
            }
        }
    });
});

// 打开新增用户窗口
function userAdd() {
    var modal = new Custombox.modal({
        content: {
            target: '#modalAdd',
            effect: 'fadein'
        }
    });
    modal.open();
}

// 打开修改用户密码窗口
function userEdit() {
    var id = getSelectedRow();
    if(null == id) {
        return;
    }
    $('#userId').val(id);
    var modal = new Custombox.modal({
        content: {
            target: '#modalEdit',
            effect: 'fadein'
        }
    });
    modal.open();
}

//用户删除
function userDel() {
    var ids = getSelectedRows();
    if (null == ids) {
        return;
    }
    swal({
        title: "确认弹窗",
        text: "确认要删除数据吗？",
        icon: "warning",
        buttons: true,
        dangerMode : true,
    }).then((flag) => {
        if(flag) {
            $.ajax({
                type: "POST",
                url: "users/delete",
                contentType: "application/json",
                beforeSend: function (request) {
                    //设置header值
                    request.setRequestHeader("token", getCookie("token"));
                },
                data: JSON.stringify(ids),
                success: function (r) {
                    checkResultCode(r.resultCode);
                    if (r.resultCode == 200) {
                        swal("删除成功", {
                            icon: "success",
                        });
                        $("#jqGrid").trigger("reloadGrid");
                    } else {
                        swal(r.message, {
                            icon: "error",
                        });
                    }
                }
            });
        }
    });
}

// 绑定新增的保存按钮
$('#saveButton').click(function () {
    if (validObjectForAdd()) {
        var userName = $('#userName').val();
        var password = $('#password').val();
        var data = {"userName": userName, "password": password};
        $.ajax({
            type: "POST",
            dataType: "json",
            url: "/users/add",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data),
            beforeSend: function (request) {
                request.setRequestHeader("token", getCookie("token"));
            },
            success: function (result) {
                console.log(result);
                checkResultCode(result.resultCode);
                if (result.resultCode == 200) {
                    closeModal();
                    alert("新增成功！");
                    reload();
                } else {
                    closeModal();
                    alert(result.message);
                }
            },
            error: function () {
                reset();
                alert("操作失败！");
            }
        });
    }
});

// 绑定修改保存的按钮
$('#editButton').click(function () {
    if(validObjectForEdit()) {
        var id = $('#userId').val();
        var password = $('#passwordEdit').val();
        var data = {"id": id, "password": password};
        $.ajax({
            type: "POST",
            dataType: "json",
            url: "/users/edit",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data),
            beforeSend: function (request) {
                request.setRequestHeader("token", getCookie("token"));
            },
            success: function (result) {
                console.log(result);
                checkResultCode(result.resultCode);
                if (result.resultCode == 200) {
                    closeModal();
                    alert("修改成功！");
                    reload();
                } else {
                    closeModal();
                    alert(result.message);
                }
            },
            error: function () {
                alert("操作失败");
                reset();
            }
        });
    }
});


/**
 *  数据验证
 */
function validObjectForAdd() {
    var userName = $('#userName').val();
    if (isNull(userName)) {
        showErrorInfo("用户名不能为空！");
        return false;
    }
    if (!validUserName(userName)) {
        showErrorInfo("用户名格式不规范！");
        return false;
    } 
    var password  = $('#password').val();
    if (isNull(password)) {
        showErrorInfo("密码不能为空！");
        return false;
    } 
    if (!validPassword(password)) {
        showErrorInfo("密码格式不规范！");
        return false;
    } 
    return true;
}

/**
 * 用户导入功能V1
 */
function importV1() {
    alert("importV1");
}

//绑定modal上的编辑按钮
$('#importV2Button').click(function () {
    var fileUrl = $("#fileUrl").val();
    $.ajax({
        type: 'POST',
        dataType: "json",
        url: 'users/importV2?fileUrl=' + fileUrl,
        contentType: "application/json; charset=utf-8",
        success: function (result) {
            checkResultCode(result.resultCode);
            console.log(result);
            if (result.resultCode == 200) {
                closeModal();
                reload();
                alert("成功导入" + result.data + "条记录！");
            }
            else {
                closeModal();
                alert(result.message);
            }
            ;
        },
        error: function () {
            reset();
            alert("操作失败");
        }
    });
});

/**
 * 用户导入功能V2
 */
function importV2() {
    //点击编辑按钮后执行操作
    var modal = new Custombox.modal({
        content: {
            effect: 'fadein',
            target: '#importV2Modal'
        }
    });
    modal.open();
}


//添加Modal关闭
$('#cancelAdd').click(function () {
    closeModal();
})

//编辑Modal关闭
$('#cancelEdit').click(function () {
    closeModal();
})

//导入Modal关闭
$('#cancelImportV2').click(function () {
    closeModal();
})

/**
 * 数据验证
 */
function validObjectForEdit() {
    var userId = $('#userId').val();
    if (isNull(userId) || userId < 1) {
        showErrorInfo("数据错误！");
        return false;
    }
    var password = $('#passwordEdit').val();
    if (isNull(password)) {
        showErrorInfo("密码不能为空!");
        return false;
    }
    if (!validPassword(password)) {
        showErrorInfo("请输入符合规范的密码!");
        return false;
    }
    return true;
}

/**
 * 关闭modal
 */
function closeModal() {
    //关闭前清空输入框数据
    reset();
    Custombox.modal.closeAll();
}

/**
 * 重置
 */
function reset() {
    //隐藏错误提示框
    $('.add-error-info').css("display", "none");
    $('.edit-error-info').css("display", "none");
    //清空数据
    $('#password').val('');
    $('#passwordEdit').val('');
    $('#userName').val('');
}

/**
 * jqGrid重新加载
 */
function reload() {
    reset();
    var page = $("#jqGrid").jqGrid('getGridParam', 'page');
    $("#jqGrid").jqGrid('setGridParam', {
        page: page
    }).trigger("reloadGrid");
}