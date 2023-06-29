import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'spec',
        data: { pageTitle: 'teacherSubjectApp.spec.home.title' },
        loadChildren: () => import('./spec/spec.routes'),
      },
      {
        path: 'teacher',
        data: { pageTitle: 'teacherSubjectApp.teacher.home.title' },
        loadChildren: () => import('./teacher/teacher.routes'),
      },
      {
        path: 'staff',
        data: { pageTitle: 'teacherSubjectApp.staff.home.title' },
        loadChildren: () => import('./staff/staff.routes'),
      },
      {
        path: 'teacher-subject',
        data: { pageTitle: 'teacherSubjectApp.teacherSubject.home.title' },
        loadChildren: () => import('./teacher-subject/teacher-subject.routes'),
      },
      {
        path: 'subject',
        data: { pageTitle: 'teacherSubjectApp.subject.home.title' },
        loadChildren: () => import('./subject/subject.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
