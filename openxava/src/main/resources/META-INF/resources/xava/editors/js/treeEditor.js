if (treeEditor == null) var treeEditor = {};

treeEditor.dialogOpen = false;
treeEditor.repeat = false;

treeEditor.initTree = function() {
    console.log("init tree");
    if ($(".xava_tree").length) {
        $(".xava_tree").each(function() {
            var oxTree = $(this);
            var application = oxTree.data("application");
            var module = oxTree.data("module");
            var collectionName = oxTree.data("collection-name");
            console.log(application + " --- " + module + " --- " + collectionName);
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
                    "state": {
                        "key": "xava_tree_state_" + collectionName
                    },
                    "plugins": ["checkbox", "dnd", "state"]
                });
            });
        });
    }
}

treeEditor.finish = function() {
    console.log("terminado");
}

openxava.addEditorInitFunction(function() {

    $(document).ready(function() {
        var oxTree;
        var tableElement;
        //para evitar reejecutar treeEditor al abrir un dialogo
        if (treeEditor.dialogOpen === true) {
            oxTree = $('.xava_tree').first();
            let tableId = "#" + oxTree.data("table-id") + "__DISABLED__";
            tableElement = $(tableId).first();
            if (tableElement.length > 0) {
                treeEditor.dialogOpen = false;
            } else {
                treeEditor.initTree();
            }
        } else {
            treeEditor.initTree();
        }

        //accion de modificar el nodo con doble click
        $('.xava_tree').on('dblclick', '.jstree-anchor', function() {
            oxTree = $(this).closest('.xava_tree');
            console.log("dblclick");
            console.log(oxTree);
            var clickedNodeId = $(this).parent().attr('id');
            var clickedNode = $(this).jstree(true).get_node(clickedNodeId);
            var actionWithArgs = "row=" + (clickedNode.original.row) + oxTree.data("action-argv");

            treeEditor.dialogOpen = true;
            openxava.executeAction(oxTree.data("application"), oxTree.data("module"), "", false, oxTree.data("action"), actionWithArgs);
        });

        //selecciona en el input invisible, para accion de eliminar y agregar nuevo
        $('.xava_tree').on('changed.jstree', function(e, data) {
            console.log($(this));
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

        const trElements = document.querySelectorAll('tr.ox-collection-list-actions a');
        trElements.forEach((aElement) => {
            aElement.addEventListener('click', function(event) {
                treeEditor.dialogOpen = true;
            });
        });

        //para drag and drop
        $(document).on('dnd_stop.vakata', function(e, data) {
			treeEditor.repeat = treeEditor.repeat === true ? false : true; 
			if (treeEditor.repeat === false) {
            var treeElement = data.data.obj.prevObject[0];
            var treeClass = treeElement.classList;
            var ref;

            var xavaTrees = $('.xava_tree');
            xavaTrees.each(function(index) {
                let xavaTreeClass = this.classList;
                if (xavaTreeClass === treeClass) {
                    oxTree = $(this);
                    ref = oxTree.jstree(true);
                }
            });
            if (ref.get_node(data.data.nodes[0]) != false) {
                var application = oxTree.data("application");
                var module = oxTree.data("module");
                var modelName = oxTree.data("model-name");
                var collectionName = oxTree.data("collection-name");
                var pathProperty = oxTree.data("path-property");
                console.log(collectionName);
                var rows = [];

                //obtener el row de los nodos a mover
                var nodeArray = data.data.nodes;
                let parentId = "";
                nodeArray.forEach(function(element) {
                    let node = ref.get_node(element);
                    parentId = parentId === "" ? node.parent : parentId;
                    rows.push(node.original.row);
                });

                //obtener el id del padre
                var nodoPadre = ref.get_node(parentId);
                newPath = nodoPadre.original.path + "/" + nodoPadre.original.id;
                Tree.updateNode(application, module, modelName, collectionName, pathProperty, newPath, rows, treeEditor.finish);
            }
			}
        });

    });

});