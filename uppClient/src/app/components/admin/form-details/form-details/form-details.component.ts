import { Component, OnInit, Input } from '@angular/core';
import { RepositoryService } from 'src/app/services/repository.service';
import { AdminService } from 'src/app/services/admin/admin.service';
import { NgSelectComponent } from '@ng-select/ng-select';
import { HttpErrorResponse } from '@angular/common/http';
import { fakeAsync } from '@angular/core/testing';

@Component({
  selector: 'app-form-details',
  templateUrl: './form-details.component.html',
  styleUrls: ['./form-details.component.css']
})
export class FormDetailsComponent implements OnInit {
  @Input() taskId: string;
  formFieldsDto = null;
  formFields = [];
  enumKeys = [];
  enumValues = [];
  processInstance = '';

  labels = [];
  names = [];
  enumerations = [];
  enumerationsValues = [];
  errorMessage: String = '';
  isReadOnly = [];
  constructor(private repositoryService: RepositoryService,
              private adminService: AdminService) { }

  ngOnInit() {
    this.repositoryService.getForm(this.taskId).subscribe(
      res => {

        // this.categories = res;
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        console.log('this.formFields: ', this.formFields);
        this.processInstance = res.processInstanceId;
        this.formFields.forEach( (field) => {
          if (field.validationConstraints.length === 0) {
            this.isReadOnly.push(false);
            console.log('nema constraint ', field);
          } else {
            field.validationConstraints.forEach((constraint) => {
              console.log('ima constraint', field);

              if (constraint.name === 'readonly') {
                this.isReadOnly.push(true);
              } 
            });
          }
         
          if ( field.type.name === 'enum') {
            this.labels.push(field.label);
            this.names.push(field.id);
            this.enumKeys = Object.keys(field.type.values);
            this.enumValues = Object.values(field.type.values);
            this.enumerations.push(this.enumKeys);
            this.enumerationsValues.push(this.enumValues);
            console.log('aaa: ' + this.enumValues);

          }
        });
        console.log('is readonly ', this.isReadOnly);

      },
      (err: HttpErrorResponse) => {
        console.log('Error occured');
        console.log('err: ', err);
        this.errorMessage = err.error.message;
      }
    );

  }

  onSubmit(value, form) {
    console.log('kliknuto dugme odluci');
      const o = new Array();
      // tslint:disable-next-line:forin
      for (const property in value) {
          console.log(property);
          console.log(value[property]);
          o.push({fieldId : property, fieldValue : value[property]});
      }

      console.log(o);
      this.adminService.decide(o, this.formFieldsDto.taskId).subscribe(
        res => {
          console.log(res);
          alert('Upešno ste doneli odluku.');
          window.location.reload();
        },
        (err: HttpErrorResponse) => {
          console.log('Error occured');
          console.log('err: ', err);
          this.errorMessage = err.error.message;
        }
      );
  }

}
