"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getBackendAuth = getBackendAuth;
exports.getBackendUrl = getBackendUrl;
exports.getDefaultCallScreening = getDefaultCallScreening;
exports.getMyNumber = getMyNumber;
exports.init = init;
exports.multiply = multiply;
exports.setBackendAuth = setBackendAuth;
exports.setBackendUrl = setBackendUrl;
exports.setDefaultCallScreening = setDefaultCallScreening;
exports.setMyNumber = setMyNumber;
var _reactNative = require("react-native");
const LINKING_ERROR = `The package 'ufotect-android-identificador-llamadas' doesn't seem to be linked. Make sure: \n\n` + _reactNative.Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const UfotectAndroidIdentificadorLlamadas = _reactNative.NativeModules.UfotectAndroidIdentificadorLlamadas ? _reactNative.NativeModules.UfotectAndroidIdentificadorLlamadas : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});
function multiply(a, b) {
  return UfotectAndroidIdentificadorLlamadas.multiply(a, b);
}
function getDefaultCallScreening() {
  return UfotectAndroidIdentificadorLlamadas.getDefaultCallScreening();
}
function setDefaultCallScreening() {
  return UfotectAndroidIdentificadorLlamadas.defaultCallScreening();
}
function getMyNumber() {
  return UfotectAndroidIdentificadorLlamadas.getMyNumber();
}
function setMyNumber(my_number) {
  return UfotectAndroidIdentificadorLlamadas.setMyNumber(my_number);
}
function getBackendUrl() {
  return UfotectAndroidIdentificadorLlamadas.getBackendUrl();
}
function setBackendUrl(url) {
  return UfotectAndroidIdentificadorLlamadas.setBackendUrl(url);
}
function getBackendAuth() {
  return UfotectAndroidIdentificadorLlamadas.getBackendAuth();
}
function setBackendAuth(token) {
  return UfotectAndroidIdentificadorLlamadas.setBackendAuth(token);
}
function init() {
  return UfotectAndroidIdentificadorLlamadas.init();
}
//# sourceMappingURL=index.js.map