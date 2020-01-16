import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgSelectModule } from '@ng-select/ng-select';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavigationComponent } from './components/basic-components/navigation/navigation.component';
import { RegisterFormComponent } from './components/user/register-form/register-form.component';
import { LoginFormComponent } from './components/user/login-form/login-form.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { SomethingComponent } from './components/admin/something/something.component';
import { FormDetailsComponent } from './components/admin/form-details/form-details/form-details.component';
import { NewJournalComponent } from './components/editor/new-journal/new-journal.component';
import { JwtInterceptor } from './auth/auth-interceptor';
import { AddEditorsAndReviewersFormComponent } from './components/editor/add-editors-and-reviewers-form/add-editors-and-reviewers-form/add-editors-and-reviewers-form.component';
import { EditorTasksComponent } from './components/editor/editor-tasks/editor-tasks.component';
import { EditJournalDataComponent } from './components/editor/edit-journal-data/edit-journal-data.component';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    RegisterFormComponent,
    LoginFormComponent,
    HomePageComponent,
    SomethingComponent,
    FormDetailsComponent,
    NewJournalComponent,
    AddEditorsAndReviewersFormComponent,
    EditorTasksComponent,
    EditJournalDataComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule { }
