import { Component, OnInit, Input } from '@angular/core';
import { AuthorService } from 'src/app/services/author/author.service';
import { HttpErrorResponse, HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { PaperService } from 'src/app/services/journal/paper.service';
import { APP_BASE_HREF } from '@angular/common';
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
  uploadedFile64: string | ArrayBuffer;
  downloadUrl;
  labels = [];
  names = [];
  enumerations = [];
  enumerationsValues = [];
  isReadOnly = [];
  isMultiSelect = [];
  isFile = [];
  isDownload = [];
  enumIsReadOnly = [];
  selected = [];

  paperId;
  loading = false;

  fd = new FormData();
  constructor(private authorService: AuthorService,
              private httpClient: HttpClient, private sanitizer: DomSanitizer,
              private paperService: PaperService) { }

  ngOnInit() {
    this.authorService.getForm(this.taskId).subscribe(
      res => {


        console.log(res);
        // this.categories = res;
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        this.processInstance = res.processInstanceId;
        this.formFields.forEach( (field) => {
          if (field.validationConstraints.length === 0) {
            this.isReadOnly.push(false);
            console.log('nema constraint ', field.id);
          }  else {
            field.validationConstraints.forEach((constraint) => {
              console.log('ima constraint', field.id);
              if (constraint.name === 'readonly') {
                console.log('ima readonly constraint', field.id);

                this.isReadOnly.push(true);
              } else {
                this.isReadOnly.push(false);
              }
            });
          }
          if (field.properties[Object.keys(field.properties)[0]] === 'fajl') {
            this.isFile.push(true);

          }if (field.id === 'radId') {
            this.paperId = field.value.value;
            console.log('RAD ID: ', this.paperId);
            //this.download();

          }  if (field.properties[Object.keys(field.properties)[0]] === 'link') {
            this.isDownload.push(true);
            console.log('DOWNLOAD field.value.value', field.value.value);

          } else {
            this.isFile.push(false);
            this.isDownload.push(false);

          }
          if ( field.type.name === 'enum') {
            this.enumKeys = Object.keys(field.type.values);
            this.enumValues = Object.values(field.type.values);
            this.labels.push(field.label);
            this.names.push(field.id);
            this.enumerations.push(this.enumKeys);
            this.enumerationsValues.push(this.enumValues);
            console.log('field.properties.value=', field.properties[Object.keys(field.properties)[0]]);
            if (field.properties[Object.keys(field.properties)[0]] === 'true') {
              this.isMultiSelect.push(true);
              this.enumIsReadOnly.push(false);
              console.log('treba multiselect ', field.id);
            } else if (field.properties[Object.keys(field.properties)[0]] === 'sakri') {
              this.enumIsReadOnly.push(true);
              this.isMultiSelect.push(false);
              console.log('sakri ', field.id);
              console.log('enumerationsValues ', this.enumerationsValues[0]);
              this.selected.push(this.enumerationsValues[0]);
              console.log('selected ', this.selected);

            } else {
              this.isMultiSelect.push(false);
              this.enumIsReadOnly.push(false);

              console.log('ne treba multiselect ', field.id);

            }

          }

        });
        console.log('is readonly ', this.isReadOnly);
        console.log('is isMultiSelect ', this.isMultiSelect);
        console.log('is enumIsReadOnly ', this.enumIsReadOnly);

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
          if (property === 'fileUpload'){
            value[property] = this.uploadedFile64;
          }
          o.push({fieldId : property, fieldValue : value[property]});
      }

      console.log(o);
      this.loading = true;

      this.authorService.submit(o, this.formFieldsDto.taskId).subscribe(
        res => {
          console.log(res);
          alert('Uspešno ste resili zadatak.');
          this.loading = false;
          window.location.reload();
        },
        (err: HttpErrorResponse) => {
          console.log('Error submit');
          console.log('err: ', err);
          this.loading = false;
          this.errorMessage = err.error.message;
        }
      );
  }

  uploadDocument(event) {
    const file: File = event.target.files[0];
    const reader = new FileReader();
    reader.onload = () => {
      this.uploadedFile64 = reader.result;
    };
    reader.readAsDataURL(file);
  }

  download() {
    console.log('rad id' , this.paperId);
    this.paperService.getPaper(this.paperId).subscribe(
      res => {
        console.log(res);
        const downloadLink = document.createElement('a');
        const fileName = 'sample.pdf';
        downloadLink.href = res;
        downloadLink.download = fileName;
        downloadLink.click();
      },
      (err: HttpErrorResponse) => {
        console.log('Error submit');
        console.log('err: ', err);
        this.errorMessage = err.error.message;
      }
    );
  }
}
