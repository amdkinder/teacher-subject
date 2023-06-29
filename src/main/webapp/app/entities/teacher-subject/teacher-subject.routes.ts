import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TeacherSubjectComponent } from './list/teacher-subject.component';
import { TeacherSubjectDetailComponent } from './detail/teacher-subject-detail.component';
import { TeacherSubjectUpdateComponent } from './update/teacher-subject-update.component';
import TeacherSubjectResolve from './route/teacher-subject-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const teacherSubjectRoute: Routes = [
  {
    path: '',
    component: TeacherSubjectComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeacherSubjectDetailComponent,
    resolve: {
      teacherSubject: TeacherSubjectResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeacherSubjectUpdateComponent,
    resolve: {
      teacherSubject: TeacherSubjectResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeacherSubjectUpdateComponent,
    resolve: {
      teacherSubject: TeacherSubjectResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default teacherSubjectRoute;
