import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaperRepositoryService {

  constructor(private httpClient: HttpClient) { }

  startPaperProcess() {
    return this.httpClient.get('http://localhost:8080/author/get') as Observable<any>;
  }
}
