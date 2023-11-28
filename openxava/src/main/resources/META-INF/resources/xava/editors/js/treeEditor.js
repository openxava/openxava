if (treeEditor == null) var treeEditor = {};

treeEditor.dialogOpen = false;
treeEditor.parentId;

treeEditor.initTree = function() {
    if ($(".xava_tree").length) {
        $(".xava_tree").each(function() {
            var oxTree = $(this);
            var application = oxTree.data("application");
            var module = oxTree.data("module");
            var collectionName = oxTree.data("collection-name");
			var state = localStorage.getItem("xava_tree_state_" + collectionName);
            Tree.getNodes(application, module, collectionName, function(array) {
                var nodes = JSON.parse(array);
                oxTree.jstree({
                    "core": {
                        "check_callback": function(operation, node, parent, position, more) {
                            if (operation === "move_node") {
                                treeEditor.parentId = parent.id;
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

$(document).on('dnd_stop.vakata', function(e, data) {
	if (data.element === data.event.target) return;
    var treeElement = data.data.origin.element[0];
    var ref;
    var oxTree;
	
    var xavaTrees = $('.xava_tree');
    xavaTrees.each(function(index) {
        let xavaTreeClass = this.classList;
        if (xavaTreeClass === treeElement.classList) {
            oxTree = $(this);
            ref = oxTree.jstree(true);
        }
    });
	
	if (!oxTree[0].contains(data.event.target)) return;
	
    var application = oxTree.data("application");
    var module = oxTree.data("module");
    var modelName = oxTree.data("model-name");
    var collectionName = oxTree.data("collection-name");
	var idProperties = oxTree.data("id-properties");
    var rows = [];
    var childRows = [];
    var allChilds = [];
    var nodeArray = data.data.nodes;

    nodeArray.forEach(function(element) {
        let node = ref.get_node(element);
        rows.push(node.original.row);
        node.children_d.forEach(function(childNodeId) {
            allChilds.push(childNodeId);
        });

    });

    if (allChilds.length > 0) {
        allChilds.forEach(function(element) {
            let node = ref.get_node(element);
            childRows.push(node.original.row);
        });
    }
    var parentNode = ref.get_node(treeEditor.parentId);
	var parents = [];
	var newPath = "";
	var nextRoot = false;
	var nodePathArray = [];
	var newPath2 = "";

	if (parentNode.id === "#") {
		newPath = "";
	} else {
		let auxNode = ref.get_node(parentNode.id);
		while(!nextRoot){
			nextRoot = (auxNode.parent === "#") ? true : false;
			parents.push(auxNode.id);
			newPath = "/" + auxNode.id + newPath; 
			auxNode = ref.get_node(auxNode.parent);
		}
	}
    Tree.updateNode(application, module, collectionName, newPath, rows, childRows);
});


openxava.addEditorInitFunction(function() {
        var oxTree;
        var tableElement;
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

        $('.xava_tree').on('dblclick', '.jstree-anchor', function() {
            oxTree = $(this).closest('.xava_tree');
            var clickedNodeId = $(this).parent().attr('id');
            var clickedNode = $(this).jstree(true).get_node(clickedNodeId);
            var actionWithArgs = "row=" + (clickedNode.original.row) + oxTree.data("action-argv");
            treeEditor.dialogOpen = true;
            openxava.executeAction(oxTree.data("application"), oxTree.data("module"), "", false, oxTree.data("action"), actionWithArgs);
        });

        $('.xava_tree').on('changed.jstree', function(e, data) {
            oxTree = $(this).closest('.xava_tree');
            if (data.hasOwnProperty('node')) {
                var actionWithArgs = "row=" + data.node.original.row + oxTree.data("action-argv");
                var htmlInput = document.querySelector('#' + oxTree.data("xava-id") + data.node.original.row + '[value="' + oxTree.data("prefix") + "selected:" + data.node.original.row + '"]');
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
		
		$('.xava_tree').on('loaded.jstree', function (){
			var collectionName = $(this).data("collection-name");
			var state = localStorage.getItem("xava_tree_state_" + collectionName);
			if (state === null) $(this).jstree().open_all();
		})

        const trElements = document.querySelectorAll('tr.ox-collection-list-actions a');
        trElements.forEach((aElement) => {
            aElement.addEventListener('click', function(event) {
                treeEditor.dialogOpen = true;
            });
        });
});