if (treeEditor == null) var treeEditor = {};

treeEditor.initTree = function() {
    if ($(".xava_tree").length) {
        $(".xava_tree").each(function() {
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
        $('.xava_tree').on('dblclick', '.jstree-anchor', function() {
            console.log("dblclick");
			oxTree = $(this).closest('.xava_tree');
            // Accede al nodo que se hizo doble clic
            var clickedNodeId = $(this).parent().attr('id');
            var clickedNode = $(this).jstree(true).get_node(clickedNodeId);

            // Realiza las acciones que deseas al hacer doble clic en el nodo
            var actionWithArgs = "row=" + (clickedNode.original.row) + oxTree.data("actionArgv");
			console.log(oxTree.data("application"));
			console.log(oxTree.data("module"));
			console.log(oxTree.data("action"));
			console.log(actionWithArgs);
			
            openxava.executeAction(oxTree.data("application"), oxTree.data("module"), "", false, oxTree.data("action"), actionWithArgs);
        });

        //selecciona en el input invisible, para accion de eliminar y agregar nuevo
        $('.xava_tree').on('changed.jstree', function(e, data) {
            console.log("changed.jstree");
			oxTree = $(this).closest('.xava_tree');
            if (data.hasOwnProperty('node')) {
                var actionWithArgs = "row=" + data.node.original.row + oxTree.data("action-argv");
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
            ref = $('.xava_tree').jstree(true);

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