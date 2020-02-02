import { Component, OnInit } from '@angular/core';
import { PaperRepositoryService } from 'src/app/services/paper-repository.service';
import { TokenStorageService } from 'src/app/auth/token-storage.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { PaperService } from 'src/app/services/journal/paper.service';

@Component({
  selector: 'app-new-paper',
  templateUrl: './new-paper.component.html',
  styleUrls: ['./new-paper.component.css']
})
export class NewPaperComponent implements OnInit {
  formFieldsDto = null;
  formFields = [];
  processInstance = '';
  enumKeys = [];
  enumValues = [];
  errorMessage = '';

  constructor(private repositoryService: PaperRepositoryService,
              private tokenStorage: TokenStorageService,
              private router: Router,
              private paperService: PaperService) {

    repositoryService.startPaperProcess().subscribe(
      res => {

        console.log(res);
        // this.categories = res;
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        this.processInstance = res.processInstanceId;
        this.formFields.forEach( (field) => {
          if ( field.type.name === 'enum') {
              this.enumKeys = Object.keys(field.type.values);
              this.enumValues = Object.values(field.type.values);
              console.log('aaa: ' + this.enumValues);
          }
        });
      },
      (err: HttpErrorResponse) => {
        console.log('error prilikom starta procesa za dodavanje radova: ', err);
        this.errorMessage = err.error.message;
      }
    );
  }

  ngOnInit() {
  }

  onSubmit(value, form) {
    console.log('kliknuto dugme za izbor casopisa u koji se dodaje rad');
      const o = new Array();
      // tslint:disable-next-line:forin
      for (const property in value) {
          console.log(property);
          console.log(value[property]);
          o.push({fieldId : property, fieldValue : value[property]});
      }

      console.log(o);
      this.paperService.chooseJournal(o, this.formFieldsDto.taskId).subscribe(
        (data) => {
            alert('Uspešno ste izabrali časopis!');
            this.router.navigate(['']);
        },
        (err: HttpErrorResponse) => {
          console.log('Error prilikom izbora casopisa');
          console.log('err: ', err);
          this.errorMessage = err.error.message;

        }
      );
  }

}
