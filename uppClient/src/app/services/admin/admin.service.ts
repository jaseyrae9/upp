import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private httpClient: HttpClient) { }

  decide(dto, taskId) {
    return this.httpClient.post('http://localhost:8080/register/decide/'.concat(taskId), dto) as Observable<any>;
  }

  getAll(): Observable<any> {
    return this.httpClient.get('http://localhost:8080/admin/all');
  }

  makeAdmin(id) {
    return this.httpClient.post('http://localhost:8080/admin/makeAdmin/' + id, httpOptions) as Observable<any>;
  }

  makeEditor(id) {
    return this.httpClient.post('http://localhost:8080/admin/makeEditor/'+ id, httpOptions) as Observable<any>;
  }
}
