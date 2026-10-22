import {Component,inject,signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {AuthService} from './auth.service';
import {ApiService} from './api.service';
@Component({selector:'app-root',standalone:true,imports:[CommonModule,FormsModule],templateUrl:'./app.component.html'})
export class AppComponent {
  auth=inject(AuthService);api=inject(ApiService);ready=signal(false);busy=signal(false);error=signal('');notice=signal('');
  view=signal('orders');orders=signal<any[]>([]);receipts=signal<any[]>([]);queues=signal<any[]>([]);nodes=signal<any[]>([]);exchanges=signal<any[]>([]);
  status=signal<any>({});
  form={product:'Kit de sensores IoT',quantity:1,email:'cliente@example.test',scenario:'NORMAL'};
  resource={name:'demo.clase',type:'direct',queue:'demo.clase',exchange:'demo.exchange',routingKey:'demo'};
  constructor(){this.start();}
  async start(){try{await this.auth.initialize();this.ready.set(true);if(this.auth.user())await this.refresh();}catch(e){this.fail(e);}}
  fail(e:unknown){this.error.set(e instanceof Error?e.message:String(e));}
  async login(){try{await this.auth.login();}catch(e){this.fail(e);}}
  async logout(){try{await this.auth.logout();}catch(e){this.fail(e);}}
  async refresh(){this.busy.set(true);this.error.set('');try{
    const [orders,receipts,queues,nodes,exchanges,status]=await Promise.all([
      this.api.request<any[]>('/api/orders'),this.api.request<any[]>('/api/workers/receipts'),this.api.request<any[]>('/api/admin/queues'),
      this.api.request<any[]>('/api/admin/nodes'),this.api.request<any[]>('/api/admin/exchanges'),this.api.request('/api/orders/status')]);
    this.orders.set(orders);this.receipts.set(receipts);this.queues.set(queues);this.nodes.set(nodes);this.exchanges.set(exchanges.filter(e=>e.name.startsWith('ea2.')||e.name.startsWith('demo.')));this.status.set(status);
  }catch(e){this.fail(e);}finally{this.busy.set(false);}}
  async createOrder(){this.busy.set(true);this.notice.set('');this.error.set('');try{
    const order=await this.api.request<any>('/api/orders','POST',this.form);
    this.notice.set(`Pedido ${order.id.slice(0,8)} registrado. Los tres eventos se procesan de forma asíncrona.`);await this.refresh();
  }catch(e){this.fail(e);}finally{this.busy.set(false);}}
  async admin(action:string){this.busy.set(true);this.error.set('');this.notice.set('');try{
    if(action==='queue')await this.api.request('/api/admin/queues','POST',{name:this.resource.name,durable:true});
    if(action==='exchange')await this.api.request('/api/admin/exchanges','POST',{name:this.resource.exchange,type:this.resource.type});
    if(action==='binding')await this.api.request('/api/admin/bindings','POST',{queue:this.resource.name,exchange:this.resource.exchange,routingKey:this.resource.routingKey});
    if(action==='unbind')await this.api.request('/api/admin/bindings','DELETE',{queue:this.resource.name,exchange:this.resource.exchange,routingKey:this.resource.routingKey});
    if(action==='deleteQueue')await this.api.request('/api/admin/queues/'+encodeURIComponent(this.resource.name),'DELETE');
    if(action==='deleteExchange')await this.api.request('/api/admin/exchanges/'+encodeURIComponent(this.resource.exchange),'DELETE');
    this.notice.set('Operación completada por la API administradora.');await this.refresh();
  }catch(e){this.fail(e);}finally{this.busy.set(false);}}
  mainQueues(){return this.queues().filter(q=>q.name.startsWith('ea2.')&&!q.name.endsWith('.dlq'));}
  dlqCount(){return this.queues().filter(q=>q.name.endsWith('.dlq')).reduce((sum,q)=>sum+(q.messages||0),0);}
  results(id:string){return this.receipts().filter(r=>r.orderId===id);}
}
