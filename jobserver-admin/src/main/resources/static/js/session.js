var Session = function () {
    let table = layui.table;
    let form = layui.form;
    let dropdown = layui.dropdown;

    let otherConfigEditor;
    return {
        init: function () {
            let cols = [
                [{
                    title: '序号',
                    type: 'numbers'
                },
                    {
                        title: '集群Code',
                        field: 'code',
                        align: 'left',
                        width: 120
                    },
                    {
                        title: '集群名称',
                        field: 'name',
                        align: 'left',
                        width: 120,
                    },
                    {
                        title: '调度类型',
                        field: 'schedulerType',
                        align: 'left',
                        width: 100,
                    },
                    {
                        title: '开启kerberos',
                        field: 'kerberosEnabled',
                        align: 'left',
                        width: 100,
                        templet: function(record) {
                            const kerberosEnabled = record.kerberosEnabled;
                            if (kerberosEnabled === 1) {
                                return '<span style="font-weight:bold; color: #5FB878">启用</span>'
                            } else {
                                return '<span style="font-weight:bold;color: #FF5722">关闭</span>'
                            }
                        }
                    },
                    {
                        title: '状态',
                        field: 'status',
                        align: 'left',
                        width: 80,
                        templet: function(record) {
                            const status = record.status;
                            if (status) {
                                return '<span style="font-weight:bold; color: #5FB878">启用</span>'
                            } else {
                                return '<span style="font-weight:bold;color: #FF5722">关闭</span>'
                            }
                        }
                    },
                    {
                        title: '更新时间',
                        field: 'gmtModified',
                        align: 'left',
                        width: 150
                    },
                    {
                        title: '操作',
                        toolbar: '#cluster-bar',
                        align: 'right',
                        width: 100,
                        fixed: "right"
                    }
                ]
            ]

            table.render({
                elem: '#session-table',
                url: '/session/queryClusters',
                page: true,
                cols: cols,
                skin: 'line',
                parseData: function (res) {
                    return {
                        "code": 0,
                        "count": res.total,
                        "data": res.rows
                    };
                },
                toolbar: '#toolbarDemo',
                defaultToolbar: [],
            });

            table.on('tool(session-table)', function(obj) {
                let data = obj.data;
                if (obj.event === 'remove') {
                    Session.closeCluster(data.id, data.code)
                } else if (obj.event === 'edit') {
                    Session.newSessionClusterWin(data.id)
                }
            });

            table.on('toolbar(session-table)', function(obj) {
                if (obj.event === 'refresh') {
                    Session.refresh();
                }
            });

            form.on('submit(user-query)', function(data) {
                table.reload('session-table', {
                    where: data.field
                })
                return false;
            });

            otherConfigEditor = Session.getEditor(otherConfigEditor, "otherConfigEditor", "ace/mode/yaml");
        },

        getEditor: function(editor, editorId, mode) {
            editor = ace.edit(editorId);
            editor.setTheme("ace/theme/cobalt");
            editor.getSession().setMode(mode);
            $('#' + editorId).height("260px");
            editor.resize();
            return editor;
        },

        setEditorValue : function(editor, config) {
            if (config == null) {
                editor.setValue("");
            } else {
                editor.setValue(config);
            }
            editor.clearSelection();
        },

        queryClusters: function () {
            $.ajax({
                async: false,
                type : "GET",
                url: '/cluster/queryClusterNames',
                success: function (rows) {
                    let html = "<option value=''>请选择集群</option>";
                    for (var i = 0, len = rows.length; i < len; i++) {
                        html += '<option value="' + rows[i].code + '">【' + rows[i].schedulerType + "】" + rows[i].name + '</option>'
                    }
                    $("#clusterCode").html(html);
                }
            })
        },

        newSessionClusterWin : function(clusterId) {
            Session.queryClusters();
            if (clusterId) {
                $.ajax({
                    async: true,
                    type : "GET",
                    url: '/session/queryCluster',
                    data: { clusterId: clusterId },
                    success: function (result) {
                        if (result.success) {
                            let data = result.data;
                            if (data.kerberosEnabled) {
                                data.kerberosEnabled = 1;
                            } else {
                                data.kerberosEnabled = 0;
                            }
                            if (data.status) {
                                data.status = 1;
                            } else {
                                data.status = 0;
                            }
                            form.val('newClusterForm', data);
                        }
                    }
                })
            } else {
                form.val('newSessionClusterForm', {code: "", name: ""});
            }

            var index = layer.open({
                type: 1,
                title: '新建集群',
                area: ['1000px', '600px'],
                shade: 0, //去掉遮罩
                resize: false,
                btnAlign: 'c',
                content: $("#newSessionClusterDiv"),
                btn: ['保存'],
                btn1: function(index, layero) {
                    let data = form.val('newSessionClusterForm');
                    if (!data.code) {
                        toastr.error("集群code不能为空");
                        return
                    }
                    if (!data.name) {
                        toastr.error("集群名称不能为空");
                        return
                    }

                    data.id = clusterId
                    $.ajax({
                        async: true,
                        type: "POST",
                        url: '/session/saveCluster',
                        data: data,
                        success: function (result) {
                            if (result.success) {
                                toastr.success("保存成功");
                                Session.refresh();
                            } else {
                                toastr.error(result.message);
                            }
                        }
                    });
                }
            });
        },

        closeCluster : function (clusterId, clusterCode) {
            layer.confirm('确定关闭: ' + clusterCode + " ?", {
                btn: ['确认','取消'],
                title: '提示'
            }, function (index) {
                layer.close(index);
                $.ajax({
                    async: true,
                    type : "POST",
                    url: '/session/closeCluster',
                    data: { clusterId: clusterId },
                    success: function (result) {
                        if (result.success) {
                            toastr.success("成功关闭集群: " + clusterCode)
                            table.reload('session-table');
                        } else {
                            toastr.error(result.message)
                        }
                    }
                })
            })
        },

        refresh : function() {
            table.reload('session-table');
        }
    };
}();

$(document).ready(function () {
    Session.init();
});
