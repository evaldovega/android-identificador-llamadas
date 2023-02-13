import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { getBackendUrl, getDefaultCallScreening, getMyNumber, init, setBackendUrl, setDefaultCallScreening, setMyNumber, setPreference } from 'ufotect-android-identificador-llamadas';

export default function App() {

  const _setDefaultCallScreening=()=>{
    setDefaultCallScreening().then((r:any)=>{
      console.log("Permiso ",r)
    }).catch((err:any)=>{
      console.error(err);
    })
  }

  const soyTuIdentificador=()=>{
    getDefaultCallScreening().then((r:any)=>{
      console.log({r})
    }).catch((error:any)=>{
      console.error(error)
    })
  }

  React.useEffect(() => {
    init()
    const SERVER_URL="https://truliapp.co/"
    //setBackendUrl('http://192.168.1.57:3000/validate')
    setPreference('backend_call_valid',SERVER_URL+'entidades/historial-llamadas/informacion-llamada/')
    setPreference('backend_call_store',SERVER_URL+'entidades/historial-llamadas/registro-llamada/')
    
    getBackendUrl().then((url:String)=>{
      console.log(url);
    })
  }, []);

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={_setDefaultCallScreening} style={{marginVertical:16,backgroundColor:'blue',padding:8,borderRadius:8}}>
        <Text style={{color:'white'}}>Establecer como APP de identificador de llamdas</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={soyTuIdentificador} style={{marginVertical:16,backgroundColor:'blue',padding:8,borderRadius:8}}>
        <Text style={{color:'white'}}>Soy tú identificador de llamadas</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={()=>getMyNumber().then((n:String)=>console.log(n))} style={{marginVertical:16,backgroundColor:'blue',padding:8,borderRadius:8}}>
        <Text style={{color:'white'}}>Mi número</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={()=>setMyNumber("3052402331").then((n:String)=>console.log(n))} style={{marginVertical:16,backgroundColor:'blue',padding:8,borderRadius:8}}>
        <Text style={{color:'white'}}>Set mi número</Text>
      </TouchableOpacity>

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
