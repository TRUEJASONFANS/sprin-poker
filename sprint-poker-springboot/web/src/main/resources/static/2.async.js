(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([[2],{P7Or:function(e,t,a){"use strict";var r=a("284h"),n=a("TqRt");Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0;var s=n(a("o0o1")),p=n(a("MVZn")),u=r(a("gzFw")),c={namespace:"rooms",state:{list:[],total:null,page:null},reducers:{save(e,t){var a=t.payload,r=a.data,n=a.total,s=a.page;return(0,p.default)({},e,{list:r,total:n,page:s})}},effects:{remove:s.default.mark(function e(t,a){var r,n,p,c,o;return s.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return r=t.payload,n=a.call,p=a.put,c=a.select,e.next=4,n(u.remove,r);case 4:return e.next=6,c(e=>e.users.page);case 6:return o=e.sent,e.next=9,p({type:"fetch",payload:{page:o}});case 9:case"end":return e.stop()}},e,this)}),create:s.default.mark(function e(t,a){var r,n,p,c,o;return s.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return r=t.payload,n=a.call,p=a.put,c=a.select,e.next=4,n(u.create,r);case 4:return e.next=6,c(e=>e.users.page);case 6:return o=e.sent,e.next=9,p({type:"fetch",payload:{page:o}});case 9:case"end":return e.stop()}},e,this)}),fetch:s.default.mark(function e(t,a){var r,n,p,c,o,l,d,i;return s.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return r=t.payload.page,n=void 0===r?1:r,p=a.call,c=a.put,e.next=4,p(u.fetch,{page:n});case 4:return o=e.sent,l=o.data,d=o.headers,i=parseInt(d["x-total-count"],10),n=parseInt(n,10),e.next=11,c({type:"save",payload:{data:l,total:isNaN(i)?1:i,page:isNaN(n)?1:n}});case 11:case"end":return e.stop()}},e,this)})},subscriptions:{setup(e){var t=e.dispatch,a=e.history;return a.listen(e=>{var a=e.pathname;e.query;"/rooms"===a&&u.w_connect(e=>{t({type:"save",payload:{data:JSON.parse(e.body),total:1,page:1}})})})}}};t.default=c}}]);