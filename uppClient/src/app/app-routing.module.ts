import { NgModule } from '@angular/core';
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginFormComponent } from './components/user/login-form/login-form.component';
import { RegisterFormComponent } from './components/user/register-form/register-form.component';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { SomethingComponent } from './components/admin/something/something.component';
import { NewJournalComponent } from './components/editor/new-journal/new-journal.component';
// tslint:disable-next-line:max-line-length
import { AddEditorsAndReviewersFormComponent } from './components/editor/add-editors-and-reviewers-form/add-editors-and-reviewers-form/add-editors-and-reviewers-form.component';
import { EditJournalDataComponent } from './components/editor/edit-journal-data/edit-journal-data.component';

const routes: Routes = [
  { path: 'login', component: LoginFormComponent},
  { path: 'register', component: RegisterFormComponent},
  { path: '', component: HomePageComponent },
  { path: 'something', component: SomethingComponent},
  { path: 'addJournal', component: NewJournalComponent},
  { path: 'addEditorsAndReviewers', component: AddEditorsAndReviewersFormComponent},
  { path: 'editJournalData', component: EditJournalDataComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }
