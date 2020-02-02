import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PaymentRequestDTO } from 'src/app/model/journal/paymentRequestDTO';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class PaperService {

  constructor(private httpClient: HttpClient) { }

  chooseJournal(o, taskId) {
    return this.httpClient.post('http://localhost:8080/author/post/'.concat(taskId), o) as Observable<any>;
  }

  buyFromThisSeller(paymentRequestDTO: PaymentRequestDTO) {
    return this.httpClient.post('http://localhost:8080/paper/pay', paymentRequestDTO, {responseType: 'text'}) as Observable<any>;
  }
}
