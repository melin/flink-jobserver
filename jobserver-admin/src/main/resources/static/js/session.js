var Session = function () {
    let table = layui.table;
    let form = layui.form;
    let dropdown = layui.dropdown;

    let otherConfigEditor;

    let flinkCompleter = {
        identifierRegexps: [/[a-zA-Z_0-9\.\$\-\u00A2-\uFFFF]/], //解决输入点启动提示
        getCompletions: function(editor, session, pos, prefix, callback) {
            let currentLine = session.getLine(pos.row)
            if (currentLine.indexOf(":") > 0) { callback(null, []); return }
            if (prefix.length === 0) { callback(null, []); return }

            callback(null, FLINK_CONFIG_OPTIONS)
        }
    }
    let langTools = ace.require("ace/ext/language_tools");
    langTools.setCompleters([flinkCompleter]);

    return {
        init: function () {
            let cols = [
                [{
                    title: '序号',
                    type: 'numbers'
                },
                    {
                        title: 'Session Name',
                        field: 'sessionName',
                        align: 'left',
                        width: 120,
                    },
                    {
                        title: 'Flink Cluster',
                        field: 'clusterCode',
                        align: 'left',
                        width: 120,
                    },
                    {
                        title: '状态',
                        field: 'status',
                        align: 'left',
                        width: 80,
                        templet: function(record) {
                            const status = record.status;
                            if (status === "running") {
                                return '<span style="font-weight:bold; color: #5FB878">运行中</span>'
                            } else if (status === "closed") {
                                return '<span style="font-weight:bold;color: #FF5722">关闭</span>'
                            } else {
                                return '<span style="font-weight:bold;color: #FF5722">初始化中</span>'
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
                if (obj.event === 'deleteSession') {
                    Session.deleteCluster(data.id, data.sessionName)
                } else if (obj.event === 'editSession') {
                    Session.newSessionClusterWin(data.id)
                } else if (obj.event === 'startSession') {
                    Session.startCluster(data.id, data.sessionName)
                } else if (obj.event === 'closeSession') {
                    Session.closeCluster(data.id, data.sessionName)
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
            editor.commands.on("afterExec", function (e) {
                if (e.command.name === "insertstring" && /^[\w.]$/.test(e.args)) {
                    editor.execCommand("startAutocomplete");
                }
            });

            editor.setTheme("ace/theme/cobalt");
            editor.getSession().setMode(mode);
            $('#' + editorId).height("260px");
            editor.resize();

            editor.setOptions({
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableLiveAutocompletion: true
            });
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
                $("#session_name").attr("readonly", "readonly");
                $.ajax({
                    async: true,
                    type : "GET",
                    url: '/session/queryCluster',
                    data: { clusterId: clusterId },
                    success: function (result) {
                        if (result.success) {
                            let data = result.data;
                            let config = JSON.parse(data.config);
                            data.numberOfTaskManagers = config.numberOfTaskManagers
                            data.jobmanagerCpu = config.jobmanagerCpu
                            data.jobmanagerMemory = config.jobmanagerMemory
                            data.taskmanagerCpu = config.taskmanagerCpu
                            data.taskmanagerMemory = config.taskmanagerMemory
                            form.val('newSessionClusterForm', data);

                            Session.setEditorValue(otherConfigEditor, config.others)
                        }
                    }
                })
            } else {
                form.val('newSessionClusterForm', {code: "", name: ""});
                $("#session_name").attr("readonly", false);
            }

            var index = layer.open({
                type: 1,
                title: '新建集群',
                area: ['1000px', '600px'],
                shade: 0, //去掉遮罩
                resize: false,
                btnAlign: 'c',
                content: $("#newSessionClusterDiv"),
                btn: ['保存', '关闭'],
                zIndex: 1111,
                btn1: function(index, layero) {
                    let data = form.val('newSessionClusterForm');
                    if (!data.sessionName) {
                        toastr.error("集群 Session Name 不能为空");
                        return
                    }
                    if (!data.clusterCode) {
                        toastr.error("Flink Cluster 不能为空");
                        return
                    }

                    var config = {};
                    config.numberOfTaskManagers = data.numberOfTaskManagers
                    config.jobmanagerCpu = data.jobmanagerCpu
                    config.jobmanagerMemory = data.jobmanagerMemory
                    config.taskmanagerCpu = data.taskmanagerCpu
                    config.taskmanagerMemory = data.taskmanagerMemory
                    config.others = $.trim(otherConfigEditor.getValue());
                    data.config = JSON.stringify(config);

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

        deleteCluster : function (clusterId, sessionName) {
            layer.confirm('确定删除Session集群: ' + sessionName + " ?", {
                btn: ['确认','取消'],
                title: '提示'
            }, function (index) {
                layer.close(index);
                $.ajax({
                    async: true,
                    type : "POST",
                    url: '/session/deleteCluster',
                    data: { clusterId: clusterId },
                    success: function (result) {
                        if (result.success) {
                            toastr.success("成功删除Session集群: " + sessionName)
                            table.reload('session-table');
                        } else {
                            toastr.error(result.message)
                        }
                    }
                })
            })
        },

        startCluster : function (clusterId, sessionName) {
            layer.confirm('确定启动Session集群: ' + sessionName + " ?", {
                btn: ['确认','取消'],
                title: '提示'
            }, function (index) {
                layer.close(index);
                $.ajax({
                    async: true,
                    type : "POST",
                    url: '/session/startCluster',
                    data: { clusterId: clusterId },
                    success: function (result) {
                        if (result.success) {
                            toastr.success("成功启动Session集群: " + sessionName)
                            table.reload('session-table');
                        } else {
                            toastr.error(result.message)
                        }
                    }
                })
            })
        },

        closeCluster : function (clusterId, sessionName) {
            layer.confirm('确定关闭Session集群: ' + sessionName + " ?", {
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
                            toastr.success("成功关闭Session集群: " + sessionName)
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
