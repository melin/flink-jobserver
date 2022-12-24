var Cluster = function () {
    let winWidth, winHeight;
    let table = layui.table;
    let form = layui.form;
    let element = layui.element;
    let dropdown = layui.dropdown;
    const maxInstanceCount = $("#maxInstanceCount").val();

    let sparkCompleter = {
        identifierRegexps: [/[a-zA-Z_0-9\.\$\-\u00A2-\uFFFF]/], //解决输入点启动提示
        getCompletions: function(editor, session, pos, prefix, callback) {
            let currentLine = session.getLine(pos.row)
            if (currentLine.indexOf(":") > 0) { callback(null, []); return }
            if (prefix.length === 0) { callback(null, []); return }

            callback(null, FLINK_CONFIG_OPTIONS)
        }
    }
    let langTools = ace.require("ace/ext/language_tools");
    langTools.setCompleters([sparkCompleter]);

    let jobserverEditor, flinkEditor, coreEditor, hdfsEditor, hiveEditor, yarnEditor, kubenetesEditor;

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
                        templet: function(record) {
                            const schedulerType = record.schedulerType;
                            if (schedulerType === 'yarn') {
                                return 'Yarn'
                            } else {
                                return 'Kubernetes'
                            }
                        }
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
                elem: '#cluster-table',
                url: '/cluster/queryClusters',
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

            table.on('tool(cluster-table)', function(obj) {
                let data = obj.data;
                if (obj.event === 'remove') {
                    Cluster.closeCluster(data.id, data.code)
                } else if (obj.event === 'edit') {
                    Cluster.newClusterWin(data.id)
                }
            });

            table.on('toolbar(cluster-table)', function(obj) {
                if (obj.event === 'refresh') {
                    Cluster.refresh();
                }
            });

            form.on('submit(user-query)', function(data) {
                table.reload('cluster-table', {
                    where: data.field
                })
                return false;
            });

            jobserverEditor = Cluster.getEditor(jobserverEditor, "jobserverEditor", "ace/mode/properties");
            flinkEditor = Cluster.getEditor(flinkEditor, "flinkEditor", "ace/mode/yaml");
            coreEditor = Cluster.getEditor(coreEditor, "coreEditor", "ace/mode/xml");
            hdfsEditor = Cluster.getEditor(hdfsEditor, "hdfsEditor", "ace/mode/xml");
            hiveEditor = Cluster.getEditor(hiveEditor, "hiveEditor", "ace/mode/xml");
            //let kerberosEditor = Cluster.getEditor(kerberosEditor, "kerberosEditor", "ace/mode/properties");

            form.on('select(schedulerType)', function (data) {
                Cluster.changeConfigTab(data.value)
            });
        },

        changeConfigTab : function (schedulerType) {
            if (schedulerType === "kubernetes") {
                element.tabDelete('config_tabs', 'yarn_tab')
                element.tabDelete('config_tabs', 'kubernetes_tab')
                element.tabAdd('config_tabs', {id: 'kubernetes_tab', title: 'Kubernetes Config',
                    content: '<div id="kubenetesEditor" style="width: 100%;" class="editor"></div>'});
                kubenetesEditor = Cluster.getEditor(kubenetesEditor, "kubenetesEditor", "ace/mode/yaml");
            } else {
                element.tabDelete('config_tabs', 'yarn_tab')
                element.tabDelete('config_tabs', 'kubernetes_tab')
                element.tabAdd('config_tabs', {id: 'yarn_tab', title: 'yarn-site.xml',
                    content: '<div id="yarnEditor" style="width: 100%;" class="editor"></div>'});
                yarnEditor = Cluster.getEditor(yarnEditor, "yarnEditor", "ace/mode/xml");
            }
        },

        getEditor: function(editor, editorId, mode) {
            editor = ace.edit(editorId);
            if ("flinkEditor" === editorId) {
                editor.commands.on("afterExec", function (e) {
                    if (e.command.name === "insertstring" && /^[\w.]$/.test(e.args)) {
                        editor.execCommand("startAutocomplete");
                    }
                });
            }

            editor.setTheme("ace/theme/cobalt");
            editor.getSession().setMode(mode);
            $('#' + editorId).height((winHeight - 285) + "px");
            editor.resize();

            if ("flinkEditor" === editorId) {
                editor.setOptions({
                    enableBasicAutocompletion: true,
                    enableSnippets: true,
                    enableLiveAutocompletion: true
                });
            }
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

        newClusterWin : function(clusterId) {
            winWidth = $(window).width() * 0.95;
            winHeight = $(window).height() * 0.95;

            if (clusterId) {
                $.ajax({
                    async: true,
                    type : "GET",
                    url: '/cluster/queryCluster',
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
                            Cluster.setEditorValue(jobserverEditor, data.jobserverConfig)
                            Cluster.setEditorValue(flinkEditor, data.flinkConfig)
                            Cluster.setEditorValue(coreEditor, data.coreConfig)
                            Cluster.setEditorValue(hdfsEditor, data.hdfsConfig)
                            Cluster.setEditorValue(hiveEditor, data.hiveConfig)

                            Cluster.changeConfigTab(data.schedulerType)
                            if (data.schedulerType === "yarn") {
                                Cluster.setEditorValue(yarnEditor, data.yarnConfig)
                            } else {
                                Cluster.setEditorValue(kubenetesEditor, data.kubernetesConfig)
                            }
                        }
                    }
                })

                $("#schedulerType").attr("disabled","disabled");
                form.render('select');
            } else {
                form.val('newClusterForm', {code: "", name: ""});
                Cluster.setEditorValue(jobserverEditor, $("#confDefaultValue").val())
                Cluster.setEditorValue(flinkEditor, "")
                Cluster.setEditorValue(coreEditor, "")
                Cluster.setEditorValue(hdfsEditor, "")
                Cluster.setEditorValue(hiveEditor, "")

                Cluster.changeConfigTab("yarn")
                Cluster.setEditorValue(yarnEditor, "")

                $("#schedulerType").removeAttr("disabled");
                form.render('select');
            }

            var index = layer.open({
                type: 1,
                title: '新建集群',
                area: [winWidth + 'px', winHeight + "px"],
                shade: 0, //去掉遮罩
                resize: false,
                btnAlign: 'c',
                content: $("#newClusterDiv"),
                btn: ['保存', '取消'],
                zIndex: 1111,
                btn1: function(index, layero) {
                    let data = form.val('newClusterForm');
                    if (!data.code) {
                        toastr.error("集群code不能为空");
                        return
                    }
                    if (!data.name) {
                        toastr.error("集群名称不能为空");
                        return
                    }

                    let jobserverConfig = $.trim(jobserverEditor.getValue());
                    let flinkConfig = $.trim(flinkEditor.getValue());
                    let coreConfig = $.trim(coreEditor.getValue());
                    let hdfsConfig = $.trim(hdfsEditor.getValue());
                    let hiveConfig = $.trim(hiveEditor.getValue());

                    let yarnConfig = "";
                    let kerberosConfig = "";
                    if (data.schedulerType === "yarn") {
                        yarnConfig = $.trim(yarnEditor.getValue());
                    } else {
                        kerberosConfig = $.trim(kubenetesEditor.getValue());
                    }

                    data.id = clusterId
                    data.jobserverConfig = jobserverConfig
                    data.flinkConfig = flinkConfig
                    data.coreConfig = coreConfig
                    data.hdfsConfig = hdfsConfig
                    data.hiveConfig = hiveConfig
                    data.yarnConfig = yarnConfig
                    data.kerberosConfig = kerberosConfig
                    $.ajax({
                        async: true,
                        type: "POST",
                        url: '/cluster/saveCluster',
                        data: data,
                        success: function (result) {
                            if (result.success) {
                                toastr.success("保存成功");
                                Cluster.refresh();
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
                    url: '/cluster/closeCluster',
                    data: { clusterId: clusterId },
                    success: function (result) {
                        if (result.success) {
                            toastr.success("成功关闭集群: " + clusterCode)
                            table.reload('cluster-table');
                        } else {
                            toastr.error(result.message)
                        }
                    }
                })
            })
        },

        refresh : function() {
            table.reload('cluster-table');
        }
    };
}();

$(document).ready(function () {
    Cluster.init();
});
