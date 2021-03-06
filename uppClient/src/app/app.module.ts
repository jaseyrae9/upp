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
import { AllJournalsPageComponent } from './pages/all-journals-page/all-journals-page.component';
import { JournalBasicDetailsComponent } from './components/journal/journal-basic-details/journal-basic-details.component';
import { JournalPageComponent } from './components/journal/journal-page/journal-page.component';
import { PaperBasicInfoComponent } from './components/journal/paper-basic-info/paper-basic-info.component';
import { AllPapersCartComponent } from './components/cart/all-papers-cart/all-papers-cart.component';
import { PaperDetailsCartComponent } from './components/cart/paper-details-cart/paper-details-cart.component';
import { AllPurchasedJournalsComponent } from './components/buyer/all-purchased-journals/all-purchased-journals.component';
import { NewPaperComponent } from './components/autor/new-paper/new-paper.component';
import { AllTasksComponent } from './components/autor/all-tasks/all-tasks.component';
import { OneTaskComponent } from './components/autor/one-task/one-task.component';
import { EditionBasicInfoComponent } from './components/journal/edition-basic-info/edition-basic-info.component';
import { AllUsersPageComponent } from './pages/all-users-page/all-users-page.component';
import { UserBasicDetailsComponent } from './pages/user-basic-details/user-basic-details.component';

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
    EditJournalDataComponent,
    AllJournalsPageComponent,
    JournalBasicDetailsComponent,
    JournalPageComponent,
    PaperBasicInfoComponent,
    AllPapersCartComponent,
    PaperDetailsCartComponent,
    AllPurchasedJournalsComponent,
    NewPaperComponent,
    AllTasksComponent,
    OneTaskComponent,
    EditionBasicInfoComponent,
    AllUsersPageComponent,
    UserBasicDetailsComponent,
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
