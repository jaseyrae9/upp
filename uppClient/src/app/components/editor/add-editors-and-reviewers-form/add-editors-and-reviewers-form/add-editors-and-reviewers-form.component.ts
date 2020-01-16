import { Component, OnInit } from '@angular/core';
import { JournalService } from 'src/app/services/journal/journal.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-editors-and-reviewers-form',
  templateUrl: './add-editors-and-reviewers-form.component.html',
  styleUrls: ['./add-editors-and-reviewers-form.component.css']
})
export class AddEditorsAndReviewersFormComponent implements OnInit {

  private formFieldsDto = null;
  private formFields = [];
  private processInstance = '';
  private enumKeys = [];
  private enumValues = [];
  errorMessage: String = '';

  private labels = [];
  private names = [];
  private enumerations = [];
  private enumerationsValues = [];

  constructor(private journalService: JournalService) {
    journalService.getTaskForm().subscribe(
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
      err => {
        console.log('Error occured');
        console.log(err);
      }
    );

  }

  ngOnInit() {
  }

  onSubmit(value, form) {
    console.log('kliknuto dugme registracija');
      const o = new Array();
      // tslint:disable-next-line:forin
      for (const property in value) {
          console.log(property);
          console.log(value[property]);

          if (value[property] === '') {
            o.push({fieldId : property, fieldValue : new Array() });
          } else {
            o.push({fieldId : property, fieldValue : value[property]});
          }
      }
      console.log('podaci koji se salju za dodavanje urednika i recenzen: ');
      console.log(o);
      this.journalService.addEditorsAndReviewers(o, this.formFieldsDto.taskId).subscribe(
        res => {
          console.log('res ', res);
          alert('Uspešno ste izabrali urednike i recenzente!');
        },
        (err: HttpErrorResponse) => {
          console.log('Error occured');
          console.log(err);
          this.errorMessage = err.error.message;

        }
      );
  }

}
