import { NativeModules, Platform } from 'react-native';
const LINKING_ERROR = `The package 'ufotect-android-identificador-llamadas' doesn't seem to be linked. Make sure: \n\n` + Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const UfotectAndroidIdentificadorLlamadas = NativeModules.UfotectAndroidIdentificadorLlamadas ? NativeModules.UfotectAndroidIdentificadorLlamadas : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});
export function multiply(a, b) {
  return UfotectAndroidIdentificadorLlamadas.multiply(a, b);
}
export function getDefaultCallScreening() {
  return UfotectAndroidIdentificadorLlamadas.getDefaultCallScreening();
}
export function setDefaultCallScreening() {
  return UfotectAndroidIdentificadorLlamadas.defaultCallScreening();
}
export function getMyNumber() {
  return UfotectAndroidIdentificadorLlamadas.getMyNumber();
}
export function setMyNumber(my_number) {
  return UfotectAndroidIdentificadorLlamadas.setMyNumber(my_number);
}
export function getBackendUrl() {
  return UfotectAndroidIdentificadorLlamadas.getBackendUrl();
}
export function setBackendUrl(url) {
  return UfotectAndroidIdentificadorLlamadas.setBackendUrl(url);
}
export function getBackendAuth() {
  return UfotectAndroidIdentificadorLlamadas.getBackendAuth();
}
export function setBackendAuth(token) {
  return UfotectAndroidIdentificadorLlamadas.setBackendAuth(token);
}
export function init() {
  return UfotectAndroidIdentificadorLlamadas.init();
}
//# sourceMappingURL=index.js.map