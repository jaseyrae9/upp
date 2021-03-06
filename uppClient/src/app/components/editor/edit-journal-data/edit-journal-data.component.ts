import { Component, OnInit } from '@angular/core';
import { JournalService } from 'src/app/services/journal/journal.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-journal-data',
  templateUrl: './edit-journal-data.component.html',
  styleUrls: ['./edit-journal-data.component.css']
})
export class EditJournalDataComponent implements OnInit {
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
  private selectedValues = [];

  constructor(private journalService: JournalService,
              private router: Router) {
    journalService.getEditJournalTasks().subscribe(
      res => {
        console.log('res: ', res);
        if (res !== null) {
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
            this.selectedValues.push(field.defaultValue);
          }

        });
        }
      },
      (err: HttpErrorResponse) => {
        console.log('Error se desio prilikom ucitavanja forme za izmenu casopisa');
        console.log(err);
        this.errorMessage = err.error.message;

      }
    );
  }

  ngOnInit() {
  }

  onSubmit(value, form) {
      console.log('kliknuto dugme izmeni');
      const o = new Array();
      // tslint:disable-next-line:forin
      for (const property in value) {
          console.log(property);
          console.log(value[property]);

          // if (value[property] === '') { // Kada se dodaju recenzenti i urednici
           // o.push({fieldId : property, fieldValue : new Array() });
          // } else {
            o.push({fieldId : property, fieldValue : value[property]});
          // }
      }
      console.log('podaci koji se salju za izmeni casopisa');
      console.log(o);
      this.journalService.editJournal(o, this.formFieldsDto.taskId).subscribe(
        res => {
          console.log('res ', res);
          alert('Uspešno ste izmenili casopis, ceka se odobrenje admina!');
          this.router.navigate(['']);
        },
        (err: HttpErrorResponse) => {
          console.log('Error se desio prilikom izmene casopisa.');
          console.log(err);
          this.errorMessage = err.error.message;
        }
      );
  }

}
