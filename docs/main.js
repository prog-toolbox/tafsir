(function(){
'use strict';Object.freeze({esVersion:6,assumingES6:!0,isWebAssembly:!1,productionMode:!0,linkerVersion:"1.17.0",fileLevelThis:this});var h;
function l(a){switch(typeof a){case "string":return"java.lang.String";case "number":return m(a)?a<<24>>24===a?"java.lang.Byte":a<<16>>16===a?"java.lang.Short":"java.lang.Integer":n(a)?"java.lang.Float":"java.lang.Double";case "boolean":return"java.lang.Boolean";case "undefined":return"java.lang.Void";default:return null===a?a.D():a instanceof p?"java.lang.Long":a&&a.$classData?a.$classData.name:null.E()}}var q=0,r=new WeakMap;
function t(a){switch(typeof a){case "string":return u(a);case "number":v||(v=new x);var b=v;var c=a|0;c===a&&-Infinity!==1/a?a=c:(b.w[0]=a,a=(b.o[0]|0)^(b.o[1]|0));return a;case "bigint":b=0;for(a<BigInt(0)&&(a=~a);a!==BigInt(0);)b^=Number(BigInt.asIntN(32,a)),a>>=BigInt(32);return b;case "boolean":return a?1231:1237;case "undefined":return 0;case "symbol":return a=a.description,void 0===a?0:u(a);default:if(null===a)return 0;b=r.get(a);void 0===b&&(q=b=q+1|0,r.set(a,b));return b}}
function m(a){return"number"===typeof a&&(a|0)===a&&1/a!==1/-0}function n(a){return"number"===typeof a&&(a!==a||Math.fround(a)===a)}function y(){}y.prototype.constructor=y;function z(){}z.prototype=y.prototype;y.prototype.n=function(){return t(this)};y.prototype.q=function(){var a=this.n();return l(this)+"@"+(+(a>>>0)).toString(16)};y.prototype.toString=function(){return this.q()};function A(a){if("number"===typeof a){this.a=Array(a);for(var b=0;b<a;b++)this.a[b]=null}else this.a=a}A.prototype=new z;
A.prototype.constructor=A;function B(){}B.prototype=A.prototype;function C(a){if("number"===typeof a){this.a=Array(a);for(var b=0;b<a;b++)this.a[b]=!1}else this.a=a}C.prototype=new z;C.prototype.constructor=C;function D(a){this.a="number"===typeof a?new Uint16Array(a):a}D.prototype=new z;D.prototype.constructor=D;function E(a){this.a="number"===typeof a?new Int8Array(a):a}E.prototype=new z;E.prototype.constructor=E;function F(a){this.a="number"===typeof a?new Int16Array(a):a}F.prototype=new z;
F.prototype.constructor=F;function G(a){this.a="number"===typeof a?new Int32Array(a):a}G.prototype=new z;G.prototype.constructor=G;function H(a){if("number"===typeof a){this.a=Array(a);for(var b=0;b<a;b++)this.a[b]=h}else this.a=a}H.prototype=new z;H.prototype.constructor=H;function I(a){this.a="number"===typeof a?new Float32Array(a):a}I.prototype=new z;I.prototype.constructor=I;function J(a){this.a="number"===typeof a?new Float64Array(a):a}J.prototype=new z;J.prototype.constructor=J;
function K(){this.r=void 0;this.i=this.e=null;this.j=0;this.x=null;this.f="";this.g=this.h=void 0;this.name="";this.s=!1;this.k=void 0}function L(a,b,c,d){var e=new K;e.e={};e.x=a;e.f=b;e.g=g=>g===e;e.name=c;e.s=!0;e.k=()=>!1;void 0!==d&&(e.h=M(e,d));return e}function N(a,b,c,d){var e=new K,g=Object.getOwnPropertyNames(c)[0];e.e=c;e.f="L"+b+";";e.g=f=>!!f.e[g];e.name=b;e.k=d||(f=>!!(f&&f.$classData&&f.$classData.e[g]));"number"!==typeof a&&(a.prototype.$classData=e);return e}
function M(a,b,c){var d=new K;b.prototype.$classData=d;var e="["+a.f;d.r=b;d.e={y:1,b:1};d.i=a;d.j=1;d.f=e;d.name=e;d.g=c||(g=>d===g);d.k=g=>g instanceof b;return d}
function O(){function a(f){if("number"===typeof f){this.a=Array(f);for(var k=0;k<f;k++)this.a[k]=null}else this.a=f}var b=P,c=new K;a.prototype=new B;a.prototype.constructor=a;a.prototype.$classData=c;var d=b.i||b,e=b.j+1;b="["+b.f;c.r=a;c.e={y:1,b:1};c.i=d;c.j=e;c.f=b;c.name=b;var g=f=>{var k=f.j;return k===e?d.g(f.i):k>e&&d===Q};c.g=g;c.k=f=>{f=f&&f.$classData;return!!f&&(f===c||g(f))};return c}var Q=new K;Q.e={};Q.f="Ljava.lang.Object;";Q.g=a=>!a.s;Q.name="java.lang.Object";Q.k=a=>null!==a;
Q.h=M(Q,A,a=>{var b=a.j;return 1===b?!a.i.s:1<b});y.prototype.$classData=Q;L(void 0,"V","void",void 0);L(!1,"Z","boolean",C);L(0,"C","char",D);L(0,"B","byte",E);L(0,"S","short",F);L(0,"I","int",G);var R=L(null,"J","long",H);L(0,"F","float",I);L(0,"D","double",J);function x(){this.w=this.o=this.l=null;v=this;this.l=new ArrayBuffer(8);this.o=new Int32Array(this.l,0,2);new Float32Array(this.l,0,2);this.w=new Float64Array(this.l,0,1);this.o[0]=16909060;new Int8Array(this.l,0,8)}x.prototype=new z;
x.prototype.constructor=x;N(x,"java.lang.FloatingPointBits$",{N:1});var v;N(0,"java.lang.Void",{T:1},a=>void 0===a);function p(a,b){this.v=a;this.u=b}p.prototype=new z;p.prototype.constructor=p;p.prototype.n=function(){return this.v^this.u};p.prototype.q=function(){S||(S=new U);var a=this.v,b=this.u;return b===a>>31?""+a:0>b?"-"+V(-a|0,0!==a?~b:-b|0):V(a,b)};N(p,"org.scalajs.linker.runtime.RuntimeLong",{A:1});
function V(a,b){if(0===(-2097152&b))b=""+(4294967296*b+ +(a>>>0));else{var c=(32+(Math.clz32(1E9)|0)|0)-(0!==b?Math.clz32(b)|0:32+(Math.clz32(a)|0)|0)|0,d=c,e=0===(32&d)?1E9<<d:0;d=0===(32&d)?5E8>>>(31-d|0)|0|0<<d:1E9<<d;var g=a,f=b;for(a=b=0;0<=c&&0!==(-2097152&f);){var k=g,w=f,Z=e,T=d;if(w===T?(-2147483648^k)>=(-2147483648^Z):(-2147483648^w)>=(-2147483648^T))k=f,w=d,f=g-e|0,k=(-2147483648^f)>(-2147483648^g)?-1+(k-w|0)|0:k-w|0,g=f,f=k,32>c?b|=1<<c:a|=1<<c;c=-1+c|0;k=d>>>1|0;e=e>>>1|0|d<<31;d=k}c=
f;if(0===c?-1147483648<=(-2147483648^g):-2147483648<=(-2147483648^c))c=4294967296*f+ +(g>>>0),g=c/1E9,e=g/4294967296|0,d=b,b=g=d+(g|0)|0,a=(-2147483648^g)<(-2147483648^d)?1+(a+e|0)|0:a+e|0,g=c%1E9|0;c=""+g;b=""+(4294967296*a+ +(b>>>0))+"000000000".substring(c.length)+c}return b}function U(){}U.prototype=new z;U.prototype.constructor=U;N(U,"org.scalajs.linker.runtime.RuntimeLong$",{B:1});var S;function W(){}W.prototype=new z;W.prototype.constructor=W;N(W,"tb.oss.tafsir.Main$",{z:1});var X;
class aa extends Error{constructor(){super();this.t=null}q(){var a=l(this),b=this.t;return null===b?a:a+": "+b}n(){return y.prototype.n.call(this)}get message(){var a=this.t;return null===a?"":a}get name(){return l(this)}toString(){return this.q()}}class ba extends aa{}N(0,"java.lang.Boolean",{G:1,b:1,c:1,d:1},a=>"boolean"===typeof a);N(0,"java.lang.Character",{J:1,b:1,c:1,d:1},()=>!1);class ca extends ba{}
class da extends ca{constructor(a){super();this.t=a;"[object Error]"!==Object.prototype.toString.call(this)&&(void 0===Error.captureStackTrace||Object.isSealed(this)?Error():Error.captureStackTrace(this))}}N(da,"java.lang.ArithmeticException",{F:1,Q:1,L:1,S:1,b:1});N(0,"java.lang.Byte",{H:1,m:1,b:1,c:1,d:1},a=>"number"===typeof a&&a<<24>>24===a&&1/a!==1/-0);N(0,"java.lang.Short",{R:1,m:1,b:1,c:1,d:1},a=>"number"===typeof a&&a<<16>>16===a&&1/a!==1/-0);
N(0,"java.lang.Double",{K:1,m:1,b:1,c:1,d:1,p:1},a=>"number"===typeof a);N(0,"java.lang.Float",{M:1,m:1,b:1,c:1,d:1,p:1},a=>n(a));N(0,"java.lang.Integer",{O:1,m:1,b:1,c:1,d:1,p:1},a=>m(a));N(0,"java.lang.Long",{P:1,m:1,b:1,c:1,d:1,p:1},a=>a instanceof p);function u(a){for(var b=0,c=1,d=-1+a.length|0;0<=d;)b=b+Math.imul(a.charCodeAt(d),c)|0,c=Math.imul(31,c),d=-1+d|0;return b}var P=N(0,"java.lang.String",{C:1,b:1,c:1,I:1,d:1,p:1},a=>"string"===typeof a);h=new p(0,0);R.x=h;P.h||(P.h=O());new P.h.r([]);
X||(X=new W);var Y=document.createElement("div");Y.textContent="Tafsir";document.body.appendChild(Y);
}).call(this);
//# sourceMappingURL=main.js.map
