// ** I18N

// Calendar ZH language
// Author: Boris Lu, <boris@de-lian.com>
// Encoding: UTF-8
// Distributed under the same terms as the calendar itself.

// full day names
Calendar._DN = new Array
("\u5468\u65e5",//\u5468\u65e5
"\u5468\u4e00",//\u5468\u4e00
"\u5468\u4e8c",//\u5468\u4e8c
"\u5468\u4e09",//\u5468\u4e09
"\u5468\u56db",//\u5468\u56db
"\u5468\u4e94",//\u5468\u4e94
"\u5468\u516d",//\u5468\u516d
"\u5468\u65e5");//\u5468\u65e5

// Please note that the following array of short day names
//and the same goes
// for short month names, _SMN) isn't absolutely necessary.
//We give it here
// for exemplification on how one can customize the short
//day names, but if
// they are simply the first N letters of the full name you
//can simply say:
//
//  Calendar._SDN_len = N; // short day name length
//  Calendar._SMN_len = N; // short month name length
//
// If N = 3 then this is not needed either since we assume a
//value of 3 if not
// present, to be compatible with translation files that
//were written before
// this feature.

// short day names
Calendar._SDN = new Array
("\u5468\u65e5",
"\u5468\u4e00",
"\u5468\u4e8c",
"\u5468\u4e09",
"\u5468\u56db",
"\u5468\u4e94",
"\u5468\u516d",
"\u5468\u65e5");

// full month names
Calendar._MN = new Array
("\u4e00\u6708",
"\u4e8c\u6708",
"\u4e09\u6708",
"\u56db\u6708",
"\u4e94\u6708",
"\u516d\u6708",
"\u4e03\u6708",
"\u516b\u6708",
"\u4e5d\u6708",
"\u5341\u6708",
"\u5341\u4e00\u6708",
"\u5341\u4e8c\u6708");

// short month names
Calendar._SMN = new Array
("\u4e00\u6708",
"\u4e8c\u6708",
"\u4e09\u6708",
"\u56db\u6708",
"\u4e94\u6708",
"\u516d\u6708",
"\u4e03\u6708",
"\u516b\u6708",
"\u4e5d\u6708",
"\u5341\u6708",
"\u5341\u4e00\u6708",
"\u5341\u4e8c\u6708");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "\u95dc\u65bc";

Calendar._TT["ABOUT"] =
"  DHTML \u65e5\u671f/\u6642\u9593\u9078\u64c7\u5de5\u5177\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + //don't translate this ;-)
"For latest version visit:\u6700\u65b0\u7248\u672c\u8acb\u5230http://www.dynarch.com/projects/calendar/\u5bdf\u770b\n"
+
"\u6388\u6b0a\u63a1\u7528GNU LGPL.  \u7ec6\u7bc0\u53c3\u95b1http://gnu.org/licenses/lgpl.html" +
"\n\n" +
"\u65e5\u671f\u9078\u64c7:\n" +
"-\u9ede\u64ca\xab(\xbb)\u6309\u9215\u9078\u64c7\u4e0a(\u4e0b)\u4e00\u5e74\u5ea6.\n"
+
"- \u9ede\u64ca" + String.fromCharCode(0x2039) + "(" +
String.fromCharCode(0x203a) +
")\u6309\u9215\u9078\u64c7\u4e0a(\u4e0b)\u500b\u6708\u4efd.\n" +
"-\u6309\u4f4f\u4e0d\u653e\u5c07\u51fa\u73fe\u66f4\u591a\u9078\u9805\u3002";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"\u6642\u9593\u9078\u64c7:\n" +
"-\u5728\u6642\u9593(\u5206\u6216\u79d2)\u4e0a\u55ae\u64ca\u6ed1\u9f20\u5de6\u9375\u4f86\u589e\u52a0\u76ee\u524d\u6642\u9593(\u5206\u6216\u8005\u79d2)\n"
+
"-\u5728\u6642\u9593(\u5206\u6216\u79d2)\u4e0a\u6309\u4f4fShift\u9375\u5f8c\u55ae\u64ca\u6ed1\u9f20\u5de6\u9375\u4f86\u6e1b\u5c11\u76ee\u524d\u6642\u9593(\u5206\u6216\u79d2)";

Calendar._TT["PREV_YEAR"] = "\u4e0a\u4e00\u5e74";
Calendar._TT["PREV_MONTH"] = "\u4e0a\u500b\u6708";
Calendar._TT["GO_TODAY"] = "\u5230\u4eca\u5929";
Calendar._TT["NEXT_MONTH"] = "\u4e0b\u500b\u6708";
Calendar._TT["NEXT_YEAR"] = "\u4e0b\u4e00\u5e74";
Calendar._TT["SEL_DATE"] = "\u9078\u64c7\u65e5\u671f";
Calendar._TT["DRAG_TO_MOVE"] = "\u62d6\u62c9";
Calendar._TT["PART_TODAY"] = " (\u4eca\u5929)";

// the following is to inform that "%s" is to be the first
//day of week
// %s will be replaced with the day name.
Calendar._TT["DAY_FIRST"] =
"%s\u70ba\u672c\u5468\u7b2c\u4e00\u5929";

// This may be locale-dependent.  It specifies the week-end
//days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6:
//0 means Sunday, 1
// means Monday, etc.
Calendar._TT["WEEKEND"] = "0,6";

Calendar._TT["CLOSE"] = "\u95dc\u9589";
Calendar._TT["TODAY"] = "\u4eca\u5929";
Calendar._TT["TIME_PART"] =
"(\u6309\u4f4fShift\u9375)\u55ae\u64ca\u6216\u62d6\u62c9\u6539\u8b8a\u503c";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Calendar._TT["TT_DATE_FORMAT"] = "%a, %b %e\u65e5";

Calendar._TT["WK"] = "\u5468";
Calendar._TT["TIME"] = "\u6642\u9593:";