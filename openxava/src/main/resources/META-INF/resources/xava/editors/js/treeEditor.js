if (treeEditor == null) var treeEditor = {};

treeEditor.initTree = function() {
    if ($(".ox_tree").length) {
        $(".ox_tree").each(function() {
            var $oxTree = $(this);
            var application = $('#xava_tree_application').val();
            var module = $('#xava_tree_module').val();
            var collectionName = $oxTree.attr("id");

            Tree.getNodes(application, module, collectionName, function(array) {
                var nodes = JSON.parse(array);
                $oxTree.jstree({
                    "core": {
                        "check_callback": function(operation, node, parent, position, more) {
                            if (operation === "move_node") {
                                return true;
                            }
                            return false;
                        },
                        "themes": {
                            "dots": true,
                            "icons": false
                        },
                        'data': nodes,
                    },
                    "checkbox": {
                        "three_state": false
                    },
                    "plugins": ["checkbox", "dnd", "state"]
                });
            });
        });
    }
}

openxava.addEditorInitFunction(function() {
    $(document).ready(function() {
        treeEditor.initTree();


/*if (treeEditor == null) var treeEditor = {};

treeEditor.nodeArray;

treeEditor.setNodes = function(array) {
    treeEditor.nodeArray = JSON.parse(array);
	console.log(array);
	console.log(treeEditor.nodeArray);
	treeEditor.initTree();
}

treeEditor.initTree = function() {
	var nodeData 
    if ($(".ox_tree").length) {
		var nodes = treeEditor.nodeArray;
        $('.ox_tree').jstree({
            "core": {
                "check_callback": function(operation, node, parent, position, more) {
                    if (operation === "move_node") {
                        return true;
                    }
                    return false;
                },
                "themes": {
                    "dots": true,
                    "icons": false
                },
                'data': nodes,
            },
            "checkbox": {
                "three_state": false
            },
            "plugins": ["checkbox", "dnd", "state"]
        });
    }
}

openxava.addEditorInitFunction(function() {
    $(document).ready(function() {
        //var $oxTree = $(".ox_tree");
        $(".ox_tree").each(function() {
            var application = $('#xava_tree_application').val();
            var module = $('#xava_tree_module').val();
            var collectionName = $(this).attr("id");
            console.log(application);
            console.log(module);
            console.log(collectionName);

            Tree.getNodes(application, module, collectionName, treeEditor.setNodes);
        });*/

/*
        if ($("#ox_tree").length) {		
            $("#ox_tree").ready(function() {
                console.log("ox tree listo");
                $('#ox_tree').jstree({
                    "core": { // core options go here
                        "check_callback": function(operation, node, parent, position, more) {
                            if (operation === "move_node") {
                                return true;
                            }
                            return false;
                        },
                        "themes": {
                            "dots": true,
                            "icons": false
                        },
                        'data': treeEditor.nodeArray,
                    },
                    "checkbox": {
                        "three_state": false
                    },
                    "plugins": ["checkbox", "dnd", "state"]
                });

            });
			*/
			

        /*
                            //accion de modificar el nodo con doble click
                            $('#ox_tree').on('dblclick', '.jstree-anchor', function() {
                                console.log("dblclick");
                                // Accede al nodo que se hizo doble clic
                                var clickedNodeId = $(this).parent().attr('id');
                                var clickedNode = $('#ox_tree').jstree(true).get_node(clickedNodeId);

                                // Realiza las acciones que deseas al hacer doble clic en el nodo
                                //console.log(clickedNode);
                                var actionWithArgs = "row=" + (clickedNode.original.row) + "<%=actionArgv%>";
                                openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', "", false, '<%=action%>', actionWithArgs);
                            });

                            //selecciona en el input invisible, para accion de eliminar y agregar nuevo
                            $('#ox_tree').on('changed.jstree', function(e, data) {
                                console.log("changed.jstree");
                                if (data.hasOwnProperty('node')) {
                                    var actionWithArgs = "row=" + data.node.original.row + "<%=actionArgv%>";
                                    console.log(actionWithArgs);
                                    var htmlInput = document.getElementById("<%=xavaId%>" + data.node.original.row);
                                    if (data.action === 'select_node') {
                                        if (htmlInput != null) {
                                            htmlInput.checked = true;
                                        }
                                    } else if (data.action === 'deselect_node') {
                                        if (htmlInput != null) {
                                            htmlInput.checked = false;
                                        }
                                    }
                                }
                            });

                            //para drag and drop
                            $(document).on('dnd_stop.vakata', function(e, data) {
                                console.log('dnd_stop.vakata');
                                ref = $('#ox_tree').jstree(true);

                                if (ref.get_node(data.data.nodes[0]) != false) {
                                    var application = "<%=request.getParameter("
                                    application ")%>";
                                    var modelName = "<%=modelName%>";
                                    var pathProperty = "<%=pathProperty%>";
                                    var nodosACambiar = [];
                                    var nodoPadre;

                                    //obtener el id de los nodos a mover
                                    var nodeArray = data.data.nodes;
                                    let parentId = "";
                                    nodeArray.forEach(function(element) {
                                        let node = ref.get_node(element);
                                        parentId = parentId === "" ? node.parent : parentId;
                                        nodosACambiar.push(node.original.id);
                                    });

                                    //obtener el id del padre
                                    nodoPadre = ref.get_node(parentId);
                                    pathAlPadre = nodoPadre.original.path + "/" + nodoPadre.original.id;
                                    //pathAlPadre = encodeURIComponent(pathAlPadre);
                                    console.log(pathAlPadre);
                                    var nodoPadre2 = "";
                                    //falta la parte por si tiene otro id distinto a id
                                    Calendar.updateNode(application, modelName, pathProperty, nodosACambiar, pathAlPadre);
                                }
                            });*/

    });
});