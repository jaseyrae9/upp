import { Component, OnInit, Input } from '@angular/core';
import { AuthorService } from 'src/app/services/author/author.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-one-task',
  templateUrl: './one-task.component.html',
  styleUrls: ['./one-task.component.css']
})
export class OneTaskComponent implements OnInit {
  @Input() taskId: string;

  formFieldsDto = null;
  formFields = [];
  processInstance = '';
  enumKeys = [];
  enumValues = [];
  errorMessage: String = '';

  labels = [];
  names = [];
  enumerations = [];
  enumerationsValues = [];


  constructor(private authorService: AuthorService) { }

  ngOnInit() {
    this.authorService.getForm(this.taskId).subscribe(
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
            this.labels.push(field.label);
            this.names.push(field.id);
            this.enumerations.push(this.enumKeys);
            this.enumerationsValues.push(this.enumValues);
          }

        });
      },
      (err: HttpErrorResponse) => {
        console.log('Error prilikom ucitavanja formi zadataka autora');
        console.log('err: ', err);
        this.errorMessage = err.error.message;
      }
    );
  }

  onSubmit(value, form) {
    console.log('kliknuto submit');
      const o = new Array();
      // tslint:disable-next-line:forin
      for (const property in value) {
          console.log(property);
          console.log(value[property]);
          o.push({fieldId : property, fieldValue : value[property]});
      }

      console.log(o);
      this.authorService.submit(o, this.formFieldsDto.taskId).subscribe(
        res => {
          console.log(res);
          alert('Uspešno ste resili zadatak.');
          window.location.reload();
        },
        (err: HttpErrorResponse) => {
          console.log('Error submit');
          console.log('err: ', err);
          this.errorMessage = err.error.message;
        }
      );
  }
}
