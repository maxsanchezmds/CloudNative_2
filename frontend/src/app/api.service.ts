import {inject,Injectable} from '@angular/core';
import {AuthService} from './auth.service';
@Injectable({providedIn:'root'})
export class ApiService {
  auth=inject(AuthService);
  async request<T=any>(path:string,method='GET',body?:unknown):Promise<T>{
    const response=await fetch(this.auth.config.apiBase+path,{method,
      headers:{'Authorization':'Bearer '+this.auth.token(),'Content-Type':'application/json'},
      ...(body===undefined?{}:{body:JSON.stringify(body)})});
    if(!response.ok){let details:any={};try{details=await response.json();}catch{}
      throw new Error(response.status===401?'La sesión no es válida. Vuelve a iniciar sesión.':
        `HTTP ${response.status}: ${details.error||details.message||'Error del servicio'}`);}
    return response.json();
  }
}
