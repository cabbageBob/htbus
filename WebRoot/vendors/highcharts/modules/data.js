!function(e){"object"==typeof module&&module.exports?module.exports=e:"function"==typeof define&&define.amd?define(["../highcharts"],e):e(Highcharts)}(function(e){var t,r=e.win,s=r.document,n=e.each,o=e.pick,i=e.inArray,a=e.splat,l=function(e,t){this.init(e,t)};e.extend(l.prototype,{init:function(e,t){this.options=e,this.chartOptions=t,this.columns=e.columns||this.rowsToColumns(e.rows)||[],this.firstRowAsNames=o(e.firstRowAsNames,!0),this.decimalRegex=e.decimalPoint&&new RegExp("^(-?[0-9]+)"+e.decimalPoint+"([0-9]+)$"),this.rawColumns=[],this.columns.length?this.dataFound():(this.parseCSV(),this.parseTable(),this.parseGoogleSpreadsheet())},getColumnDistribution:function(){var r,s=this.chartOptions,o=this.options,i=[],a=function(t){return(e.seriesTypes[t||"line"].prototype.pointArrayMap||[0]).length},l=function(t){return e.seriesTypes[t||"line"].prototype.pointArrayMap},u=s&&s.chart&&s.chart.type,h=[],m=[],d=0;n(s&&s.series||[],function(e){h.push(a(e.type||u))}),n(o&&o.seriesMapping||[],function(e){i.push(e.x||0)}),0===i.length&&i.push(0),n(o&&o.seriesMapping||[],function(e){var n,o=new t,i=h[d]||a(u),p=s&&s.series||[],c=p[d]||{},f=l(c.type||u)||["y"];o.addColumnReader(e.x,"x");for(n in e)e.hasOwnProperty(n)&&"x"!==n&&o.addColumnReader(e[n],n);for(r=0;i>r;r++)o.hasReader(f[r])||o.addColumnReader(void 0,f[r]);m.push(o),d++});var p=l(u);void 0===p&&(p=["y"]),this.valueCount={global:a(u),xColumns:i,individual:h,seriesBuilders:m,globalPointArrayMap:p}},dataFound:function(){this.options.switchRowsAndColumns&&(this.columns=this.rowsToColumns(this.columns)),this.getColumnDistribution(),this.parseTypes(),this.parsed()!==!1&&this.complete()},parseCSV:function(){var e,t,r=this,s=this.options,o=s.csv,i=this.columns,a=s.startRow||0,l=s.endRow||Number.MAX_VALUE,u=s.startColumn||0,h=s.endColumn||Number.MAX_VALUE,m=0;o&&(t=o.replace(/\r\n/g,"\n").replace(/\r/g,"\n").split(s.lineDelimiter||"\n"),e=s.itemDelimiter||(-1!==o.indexOf("	")?"	":","),n(t,function(t,s){var o,d=r.trim(t),p=0===d.indexOf("#"),c=""===d;s>=a&&l>=s&&!p&&!c&&(o=t.split(e),n(o,function(e,t){t>=u&&h>=t&&(i[t-u]||(i[t-u]=[]),i[t-u][m]=e)}),m+=1)}),this.dataFound())},parseTable:function(){var e=this.options,t=e.table,r=this.columns,o=e.startRow||0,i=e.endRow||Number.MAX_VALUE,a=e.startColumn||0,l=e.endColumn||Number.MAX_VALUE;t&&("string"==typeof t&&(t=s.getElementById(t)),n(t.getElementsByTagName("tr"),function(e,t){t>=o&&i>=t&&n(e.children,function(e,s){("TD"===e.tagName||"TH"===e.tagName)&&s>=a&&l>=s&&(r[s-a]||(r[s-a]=[]),r[s-a][t-o]=e.innerHTML)})}),this.dataFound())},parseGoogleSpreadsheet:function(){var e,t,r=this,s=this.options,n=s.googleSpreadsheetKey,o=this.columns,i=s.startRow||0,a=s.endRow||Number.MAX_VALUE,l=s.startColumn||0,u=s.endColumn||Number.MAX_VALUE;n&&jQuery.ajax({dataType:"json",url:"https://spreadsheets.google.com/feeds/cells/"+n+"/"+(s.googleSpreadsheetWorksheet||"od6")+"/public/values?alt=json-in-script&callback=?",error:s.error,success:function(s){var n,h,m=s.feed.entry,d=m.length,p=0,c=0;for(h=0;d>h;h++)n=m[h],p=Math.max(p,n.gs$cell.col),c=Math.max(c,n.gs$cell.row);for(h=0;p>h;h++)h>=l&&u>=h&&(o[h-l]=[],o[h-l].length=Math.min(c,a-i));for(h=0;d>h;h++)n=m[h],e=n.gs$cell.row-1,t=n.gs$cell.col-1,t>=l&&u>=t&&e>=i&&a>=e&&(o[t-l][e-i]=n.content.$t);r.dataFound()}})},trim:function(e,t){return"string"==typeof e&&(e=e.replace(/^\s+|\s+$/g,""),t&&/^[0-9\s]+$/.test(e)&&(e=e.replace(/\s/g,"")),this.decimalRegex&&(e=e.replace(this.decimalRegex,"$1.$2"))),e},parseTypes:function(){for(var e=this.columns,t=e.length;t--;)this.parseColumn(e[t],t)},parseColumn:function(e,t){var r,s,n,o,l,u,h,m=this.rawColumns,d=this.columns,p=e.length,c=this.firstRowAsNames,f=-1!==i(t,this.valueCount.xColumns),g=[],v=this.chartOptions,x=this.options.columnTypes||[],y=x[t],C=f&&(v&&v.xAxis&&"category"===a(v.xAxis)[0].type||"string"===y);for(m[t]||(m[t]=[]);p--;)r=g[p]||e[p],n=this.trim(r),o=this.trim(r,!0),s=parseFloat(o),void 0===m[t][p]&&(m[t][p]=n),C||0===p&&c?e[p]=n:+o===s?(e[p]=s,s>31536e6&&"float"!==y?e.isDatetime=!0:e.isNumeric=!0,void 0!==e[p+1]&&(h=s>e[p+1])):(l=this.parseDate(r),f&&"number"==typeof l&&!isNaN(l)&&"float"!==y?(g[p]=r,e[p]=l,e.isDatetime=!0,void 0!==e[p+1]&&(u=l>e[p+1],u!==h&&void 0!==h&&(this.alternativeFormat?(this.dateFormat=this.alternativeFormat,p=e.length,this.alternativeFormat=this.dateFormats[this.dateFormat].alternative):e.unsorted=!0),h=u)):(e[p]=""===n?null:n,0!==p&&(e.isDatetime||e.isNumeric)&&(e.mixed=!0)));if(f&&e.mixed&&(d[t]=m[t]),f&&h&&this.options.sort)for(t=0;t<d.length;t++)d[t].reverse(),c&&d[t].unshift(d[t].pop())},dateFormats:{"YYYY-mm-dd":{regex:/^([0-9]{4})[\-\/\.]([0-9]{2})[\-\/\.]([0-9]{2})$/,parser:function(e){return Date.UTC(+e[1],e[2]-1,+e[3])}},"dd/mm/YYYY":{regex:/^([0-9]{1,2})[\-\/\.]([0-9]{1,2})[\-\/\.]([0-9]{4})$/,parser:function(e){return Date.UTC(+e[3],e[2]-1,+e[1])},alternative:"mm/dd/YYYY"},"mm/dd/YYYY":{regex:/^([0-9]{1,2})[\-\/\.]([0-9]{1,2})[\-\/\.]([0-9]{4})$/,parser:function(e){return Date.UTC(+e[3],e[1]-1,+e[2])}},"dd/mm/YY":{regex:/^([0-9]{1,2})[\-\/\.]([0-9]{1,2})[\-\/\.]([0-9]{2})$/,parser:function(e){return Date.UTC(+e[3]+2e3,e[2]-1,+e[1])},alternative:"mm/dd/YY"},"mm/dd/YY":{regex:/^([0-9]{1,2})[\-\/\.]([0-9]{1,2})[\-\/\.]([0-9]{2})$/,parser:function(e){return Date.UTC(+e[3]+2e3,e[1]-1,+e[2])}}},parseDate:function(e){var t,r,s,n,o=this.options.parseDate,i=this.options.dateFormat||this.dateFormat;if(o)t=o(e);else if("string"==typeof e){if(i)s=this.dateFormats[i],n=e.match(s.regex),n&&(t=s.parser(n));else for(r in this.dateFormats)if(s=this.dateFormats[r],n=e.match(s.regex)){this.dateFormat=i=r,this.alternativeFormat=s.alternative,t=s.parser(n);break}n||(n=Date.parse(e),"object"==typeof n&&null!==n&&n.getTime?t=n.getTime()-6e4*n.getTimezoneOffset():"number"!=typeof n||isNaN(n)||(t=n-6e4*new Date(n).getTimezoneOffset()))}return t},rowsToColumns:function(e){var t,r,s,n,o;if(e)for(o=[],r=e.length,t=0;r>t;t++)for(n=e[t].length,s=0;n>s;s++)o[s]||(o[s]=[]),o[s][t]=e[t][s];return o},parsed:function(){return this.options.parsed?this.options.parsed.call(this,this.columns):void 0},getFreeIndexes:function(e,t){var r,s,n,o=[],i=[];for(s=0;e>s;s+=1)o.push(!0);for(r=0;r<t.length;r+=1)for(n=t[r].getReferencedColumnIndexes(),s=0;s<n.length;s+=1)o[n[s]]=!1;for(s=0;s<o.length;s+=1)o[s]&&i.push(s);return i},complete:function(){var e,r,s,n,o,a,l,u,h,m,d,p,c=this.columns,f=[],g=this.options,v=[];if(f.length=c.length,g.complete||g.afterComplete){for(n=0;n<c.length;n++)this.firstRowAsNames&&(c[n].name=c[n].shift());for(r=[],m=this.getFreeIndexes(c.length,this.valueCount.seriesBuilders),l=0;l<this.valueCount.seriesBuilders.length;l++)h=this.valueCount.seriesBuilders[l],h.populateColumns(m)&&v.push(h);for(;m.length>0;){for(h=new t,h.addColumnReader(0,"x"),p=i(0,m),-1!==p&&m.splice(p,1),n=0;n<this.valueCount.global;n++)h.addColumnReader(void 0,this.valueCount.globalPointArrayMap[n]);h.populateColumns(m)&&v.push(h)}if(v.length>0&&v[0].readers.length>0&&(d=c[v[0].readers[0].columnIndex],void 0!==d&&(d.isDatetime?e="datetime":d.isNumeric||(e="category"))),"category"===e)for(l=0;l<v.length;l++)for(h=v[l],a=0;a<h.readers.length;a++)"x"===h.readers[a].configName&&(h.readers[a].configName="name");for(l=0;l<v.length;l++){for(h=v[l],s=[],o=0;o<c[0].length;o++)s[o]=h.read(c,o);r[l]={data:s},h.name&&(r[l].name=h.name),"category"===e&&(r[l].turboThreshold=0)}u={series:r},e&&(u.xAxis={type:e}),g.complete&&g.complete(u),g.afterComplete&&g.afterComplete(u)}}}),e.Data=l,e.data=function(e,t){return new l(e,t)},e.wrap(e.Chart.prototype,"init",function(t,r,s){var n=this;r&&r.data?e.data(e.extend(r.data,{afterComplete:function(o){var i,a;if(r.hasOwnProperty("series"))if("object"==typeof r.series)for(i=Math.max(r.series.length,o.series.length);i--;)a=r.series[i]||{},r.series[i]=e.merge(a,o.series[i]);else delete r.series;r=e.merge(o,r),t.call(n,r,s)}}),r):t.call(n,r,s)}),t=function(){this.readers=[],this.pointIsArray=!0},t.prototype.populateColumns=function(e){var t=this,r=!0;return n(t.readers,function(t){void 0===t.columnIndex&&(t.columnIndex=e.shift())}),n(t.readers,function(e){void 0===e.columnIndex&&(r=!1)}),r},t.prototype.read=function(e,t){var r,s=this,o=s.pointIsArray,i=o?[]:{};return n(s.readers,function(r){var s=e[r.columnIndex][t];o?i.push(s):i[r.configName]=s}),void 0===this.name&&s.readers.length>=2&&(r=s.getReferencedColumnIndexes(),r.length>=2&&(r.shift(),r.sort(),this.name=e[r.shift()].name)),i},t.prototype.addColumnReader=function(e,t){this.readers.push({columnIndex:e,configName:t}),"x"!==t&&"y"!==t&&void 0!==t&&(this.pointIsArray=!1)},t.prototype.getReferencedColumnIndexes=function(){var e,t,r=[];for(e=0;e<this.readers.length;e+=1)t=this.readers[e],void 0!==t.columnIndex&&r.push(t.columnIndex);return r},t.prototype.hasReader=function(e){var t,r;for(t=0;t<this.readers.length;t+=1)if(r=this.readers[t],r.configName===e)return!0}});