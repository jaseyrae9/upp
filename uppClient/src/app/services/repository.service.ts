import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RepositoryService {

  constructor(private httpClient: HttpClient) { }

  startProcess() {
    return this.httpClient.get('http://localhost:8080/register/get') as Observable<any>;
  }

  getForm(processInstance: string) {
    return this.httpClient.get('http://localhost:8080/register/something/'.concat(processInstance)) as Observable<any>;
  }

  getTasks() {
    return this.httpClient.get('http://localhost:8080/register/get/tasks') as Observable<any>;
  }

}

