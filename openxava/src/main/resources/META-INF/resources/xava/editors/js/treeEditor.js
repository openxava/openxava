if (treeEditor == null) var treeEditor = {};

treeEditor.dialogOpen = false;
treeEditor.mousePosition = { x: 0, y: 0 };
treeEditor.parentId;
//treeEditor.childArray;

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
								treeEditor.parentId = parent.id;
								
								//treeEditor.childArray = node.children_d;
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

treeEditor.finish = function() { }
	
 $(document).on('dnd_stop.vakata', function(e, data) {
            var treeElement = data.data.origin.element[0];
            var treeClass = treeElement.classList;
            var ref;
			var oxTree;
			
            var xavaTrees = $('.xava_tree');
            xavaTrees.each(function(index) {
                let xavaTreeClass = this.classList;
                if (xavaTreeClass === treeClass) {
                    oxTree = $(this);
                    ref = oxTree.jstree(true);
                }
            });
			
                var application = oxTree.data("application");
                var module = oxTree.data("module");
                var modelName = oxTree.data("model-name");
                var collectionName = oxTree.data("collection-name");
                var pathProperty = oxTree.data("path-property");
                var rows = [];
				var childRows = [];
				var allChilds = [];

				console.log("nodos supuestamente seleccionados");
				console.log(data.data.nodes);
                //obtener el row de los nodos a mover
                var nodeArray = data.data.nodes;
                let parentId = "";
                nodeArray.forEach(function(element) {
                    let node = ref.get_node(element);
					rows.push(node.original.row);
					node.children_d.forEach(function(childNodeId){
						console.log(childNodeId);
						allChilds.push(childNodeId);
					});
                    
                });

				//obtener el id del padre
                var nodoPadre = ref.get_node(treeEditor.parentId);
				console.log(nodoPadre);
                newPath = nodoPadre.original.path + "/" + nodoPadre.original.id;
				
				//obtener row de los nodos hijos
				if (allChilds.length > 0) {
				    allChilds.forEach(function(element) {
                    let node = ref.get_node(element);
                    childRows.push(node.original.row);
                });
				
				}
				
				
                //Tree.updateNode(application, module, collectionName, pathProperty, newPath, rows, treeEditor.finish);
				Tree.updateNode(application, module, collectionName, pathProperty, newPath, rows, childRows);
            
});
	

openxava.addEditorInitFunction(function() {
	
	document.addEventListener('mousemove', function(e) {
    // Actualiza las coordenadas del mouse
    treeEditor.mousePosition.x = e.clientX;
    treeEditor.mousePosition.y = e.clientY;
});

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
            var clickedNodeId = $(this).parent().attr('id');
            var clickedNode = $(this).jstree(true).get_node(clickedNodeId);
            var actionWithArgs = "row=" + (clickedNode.original.row) + oxTree.data("action-argv");

            treeEditor.dialogOpen = true;
            openxava.executeAction(oxTree.data("application"), oxTree.data("module"), "", false, oxTree.data("action"), actionWithArgs);
        });

        //selecciona en el input invisible, para accion de eliminar y agregar nuevo
        $('.xava_tree').on('changed.jstree', function(e, data) {
            oxTree = $(this).closest('.xava_tree');
			console.log(data);
            if (data.hasOwnProperty('node')) {
                var actionWithArgs = "row=" + data.node.original.row + oxTree.data("action-argv");
                //var htmlInput = document.getElementById(oxTree.data("xava-id") + data.node.original.row);
				//var htmlInput = document.querySelector('#' + id + '[value="' + value + '"]');
				var htmlInput = document.querySelector('#' + oxTree.data("xava-id") + data.node.original.row + '[value="' + oxTree.data("prefix") + "selected:" + data.node.original.row + '"]');
				console.log(actionWithArgs);
				console.log(oxTree.data("xava-id") + data.node.original.row);
				console.log(htmlInput);
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
    });
});