/*!
* sorttable
*
* @Version 1.0.3
*
* Copyright (c) 2013, David Brink dbrink@gmail.com
* Copyright (c) 2010, Andres Koetter 
* Dual licensed under the MIT (MIT-LICENSE.txt)
* and GPL (GPL-LICENSE.txt) licenses.
*
* Inspired by the the dragtable from Dan Vanderkam (danvk.org/dragtable/)
* Thanks to the jquery and jqueryui comitters
* 
* Any comment, bug report, feature-request is welcome
* Feel free to contact me.
*/
(function ($) {
    $.widget("extend.sorttable", $.ui.sortable, {
        widgetEventPrefix: "sorttable",
        options: {
            helper: "table",
            helperCells: 1
        },
        _table: null,
        _startIndex: 0,
        _endIndex: 0,
        _currentItemWidth: 0,
        _sortCells: null,
        _createHelper: function (event) {
            var o = this.options;

            if (o.helper == 'table') {
                // if using 'table' helper
                if (!this.setWidths) {
                    this.setWidths = true;
                    var items = this.items;
                    var widths = [];
                    for (i = 0; i < items.length; i++) {
                        var item = $(items[i].item[0]);
                        widths[i] = item.innerWidth() - parseInt(item.css('paddingLeft') || 0, 10) - parseInt(item.css('paddingRight') || 0, 10);
                    }
                    for (i = 0; i < items.length; i++) {
                        var item = $(items[i].item[0]);
                        item.width(widths[i]);
                    }
                }

                return this._createHelperTable(event, this.currentItem);
            }
            else {
                // use default helper method
                return $.ui.sortable.prototype._createHelper.apply(this, [event]);
            }
        },
        _createHelperTable: function (e, ui) {
            var o = this.options, helperCells = o.helperCells;
            var hc = ui.is('th') || ui.is('td') ? ui : ui.parents('th:first,td:first');
            var index = hc.prevAll().length + 1
            var hcWidth = hc.innerWidth() - parseInt(hc.css('paddingLeft') || 0, 10) - parseInt(hc.css('paddingRight') || 0, 10);
            // save sizes because <ie9 reports wrong values when display:none
            this._currentItemWidth = hcWidth;
            var table = this._table;

            var helperCellCount;
            if (typeof helperCells == 'number') {
                helperCellCount = helperCells;
            }

            var cells = $([]);
            if (helperCellCount != 1) {
                cells = table.children().find('>tr:not(.ui-sortable)>td:nth-child(' + index + ')');
                if (helperCellCount < 0) {
                    cells = cells.slice(0, cells.length + helperCellCount);
                }
                else if (helperCellCount > 1) {
                    cells = cells.slice(0, helperCellCount - 1);
                }
            }

            if (!helperCellCount && helperCells) {
                cells = cells.filter(helperCells);
            }

            cells.splice(0, 0, hc); // insert first cell

            this._sortCells = cells;

            var tableClone = table.clone().empty();
            tableClone.css('position', 'absolute');
            tableClone.css('width', 'auto');
            tableClone.css('min-width', 'inherit');
            tableClone.css('height', 'auto');
            tableClone.css('min-height', 'inherit');
            tableClone.attr('id', '');
            for (i = 0; i < cells.length; i++) {
                var cell = cells[i];
                if (!(cell instanceof jQuery)) {
                    cell = $(cell);
                }
                var tr = cell.parents('tr:first');
                var cellClone = cell.clone();
                cellClone.width(hcWidth);
                var trClone = tr.clone().empty();
                var trHeight = tr.innerHeight() - parseInt(tr.css('paddingTop') || 0, 10) - parseInt(tr.css('paddingBottom') || 0, 10);
                trClone.height(trHeight);
                cellClone.appendTo(trClone.appendTo(tableClone));
            }

            tableClone.find().each(function () {
                this.attr('id', ''); // clear ids on cloned table
            });
            table.before(tableClone);

            return tableClone;
        },
        _createPlaceholder: function (that) {
            var self = that || this, o = self.options;

            // call base method
            $.ui.sortable.prototype._createPlaceholder.apply(this, arguments);

            if (o.helper == 'table') {
                var p = self.placeholder;
                // force width as border will prevent width from setting even though cell has no explicit width
                self.placeholder.width(self._currentItemWidth);

                var sortCells = self._sortCells;
                if (sortCells) {
                    sortCells.hide();
                    if (sortCells.length > 1) {
                        p.attr('rowSpan', sortCells.length);
                    }
                }

                var rowSpan = self._placeholderRowSpan;
                if (rowSpan > 1) {
                    p.attr('rowSpan', rowSpan);
                }
            }
        },
        _swapNodes: function (a, b) {
            if (a && b) {
                var aparent = a.parentNode;
                var asibling = a.nextSibling === b ? a : a.nextSibling;
                b.parentNode.insertBefore(a, b);
                aparent.insertBefore(b, asibling);
            }
        },
        // bubble the moved col left or right
        _bubbleCols: function () {
            var from = this._startIndex;
            var to = this._endIndex;
            /* Find children thead and tbody.
            * Only to process the immediate tr-children. Bugfix for inner tables
            */
            var trs = this._table.children().find('> tr:not(.ui-sortable)');
            if (from < to) {
                for (var i = from; i < to; i++) {
                    var row1 = trs.find('>td:nth-child(' + i + '),>th:nth-child(' + i + ')');
                    var row2 = trs.find('>td:nth-child(' + (i + 1) + '),>th:nth-child(' + (i + 1) + ')')
                    for (var j = 0; j < row1.length; j++) {
                        this._swapNodes(row1[j], row2[j]);
                    }
                }
            }
            else {
                for (var i = from; i > to; i--) {
                    var row1 = trs.find('>td:nth-child(' + i + '),>th:nth-child(' + i + ')');
                    var row2 = trs.find('>td:nth-child(' + (i - 1) + '),>th:nth-child(' + (i - 1) + ')')
                    for (var j = 0; j < row1.length; j++) {
                        this._swapNodes(row1[j], row2[j]);
                    }
                }
            }
        },
        _rearrangeTableBackroundProcessing: function () {
            var _this = this;
            return function () {
                _this._bubbleCols();
            };
        },
        _sortStart: function () {
            var _this = this;
            return function (e, ui) {
                _this._startIndex = ui.item.prevAll().length + 1;
            }
        },
        _sortUpdate: function () {
            var _this = this;
            return function (e, ui) {
                _this._endIndex = ui.item.prevAll().length + 1;
                if (_this._startIndex != _this._endIndex) {
                    // do reorganisation asynchronous
                    // for chrome a little bit more than 1 ms because we want to force a rerender
                    setTimeout(_this._rearrangeTableBackroundProcessing(), 50);
                }
            }
        },
        _sortStop: function () {
            var _this = this;
            return function (e, ui) {
                if (_this._sortCells) {
                    _this._sortCells.show();
                }
            }
        },
        _create: function () {
            var el = this.element;
            if (el.is('table')) {
                this._table = el;
                // reset element to first tr
                el = this.element = el.find('tr:first');
            }
            else {
                // find parent table
                this._table = el.parents('table:first');
            }

            this._startIndex = 0;
            this._endIndex = 0;

            // bind to start and update
            el.bind('sorttablestart', this._sortStart());
            el.bind('sorttableupdate', this._sortUpdate());
            el.bind('sorttablestop', this._sortStop());

            $.ui.sortable.prototype._create.apply(this);
        },
        destroy: function () {
            $.Widget.prototype.destroy.apply(this, arguments); // default destroy
            // now do other stuff particular to this widget
        }
    });
})(jQuery);