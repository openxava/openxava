if (treeEditor == null) var treeEditor = {};

treeEditor.initTree = function() {
    if ($(".ox_tree").length) {
        $(".ox_tree").each(function() {
            var oxTree = $(this);
            var application = oxTree.data("application");
            var module = oxTree.data("module");
            var collectionName = oxTree.data("collection-name");

            Tree.getNodes(application, module, collectionName, function(array) {
                var nodes = JSON.parse(array);
                oxTree.jstree({
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
		var oxTree;
        treeEditor.initTree();

        //accion de modificar el nodo con doble click
        $('.ox_tree').on('dblclick', '.jstree-anchor', function() {
            console.log("dblclick");
			oxTree = $(this).closest('.ox_tree');
            // Accede al nodo que se hizo doble clic
            var clickedNodeId = $(this).parent().attr('id');
            var clickedNode = $(this).jstree(true).get_node(clickedNodeId);

            // Realiza las acciones que deseas al hacer doble clic en el nodo
            console.log(oxTree.data("actionArgv"));
			console.log(oxTree.data("action-argv"));
			console.log(oxTree.data("collection-name"));
            var actionWithArgs = "row=" + (clickedNode.original.row) + oxTree.data("actionArgv");
			console.log("js");
			console.log(clickedNode);
			console.log(actionWithArgs);
            openxava.executeAction(oxTree.data("application"), oxTree.data("module"), "", false, oxTree.data("action"), actionWithArgs);
        });

        //selecciona en el input invisible, para accion de eliminar y agregar nuevo
        $('.ox_tree').on('changed.jstree', function(e, data) {
            console.log("changed.jstree");
			oxTree = $(this).closest('.ox_tree');
            if (data.hasOwnProperty('node')) {
                var actionWithArgs = "row=" + data.node.original.row + oxTree.data("action-argv");
                console.log(actionWithArgs);
                var htmlInput = document.getElementById(oxTree.data("xava-id") + data.node.original.row);
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
/*
        //para drag and drop
        $(document).on('dnd_stop.vakata', function(e, data) {
            console.log('dnd_stop.vakata');
            ref = $('.ox_tree').jstree(true);

            if (ref.get_node(data.data.nodes[0]) != false) {
                var application = oxTree.data("application")
                var modelName = oxTree.data("model");
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