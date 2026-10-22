import {Injectable,signal} from '@angular/core';
import {User,UserManager,WebStorageStateStore} from 'oidc-client-ts';
export interface RuntimeConfig {apiBase:string;authority:string;clientId:string;scope:string;environment:string;logoutUrl?:string;}
@Injectable({providedIn:'root'})
export class AuthService {
  user=signal<User|null>(null); config!:RuntimeConfig; private manager!:UserManager;
  async initialize(){
    const response=await fetch('/config.json',{cache:'no-store'});
    if(!response.ok)throw new Error('No fue posible cargar la configuración.');
    this.config=await response.json();
    this.manager=new UserManager({authority:this.config.authority,client_id:this.config.clientId,
      redirect_uri:location.origin+'/callback',post_logout_redirect_uri:location.origin,
      response_type:'code',scope:this.config.scope,userStore:new WebStorageStateStore({store:sessionStorage}),
      automaticSilentRenew:false,loadUserInfo:false});
    this.manager.events.addAccessTokenExpired(()=>this.user.set(null));
    if(location.pathname==='/callback'){
      this.user.set(await this.manager.signinRedirectCallback()); history.replaceState({},'', '/');
    }else this.user.set(await this.manager.getUser());
    if(this.user()?.expired)this.user.set(null);
  }
  login(){return this.manager.signinRedirect();}
  async logout(){
    const idToken=this.user()?.id_token;
    await this.manager.removeUser();this.user.set(null);
    if(this.config.logoutUrl){
      const target=new URL(this.config.logoutUrl);
      target.searchParams.set('client_id',this.config.clientId);
      target.searchParams.set('logout_uri',location.origin);
      location.assign(target.href);
    }else await this.manager.signoutRedirect({id_token_hint:idToken});
  }
  token(){const user=this.user();if(!user||user.expired)throw new Error('Inicia sesión para consultar los servicios.');return user.access_token;}
}
