import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class JournalRepositoryService {

  constructor(private httpClient: HttpClient) { }

  startJournalProcess() {
    return this.httpClient.get('http://localhost:8080/journal/get') as Observable<any>;
  }
}
