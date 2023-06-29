import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeacherSubject } from '../teacher-subject.model';
import { TeacherSubjectService } from '../service/teacher-subject.service';

export const teacherSubjectResolve = (route: ActivatedRouteSnapshot): Observable<null | ITeacherSubject> => {
  const id = route.params['id'];
  if (id) {
    return inject(TeacherSubjectService)
      .find(id)
      .pipe(
        mergeMap((teacherSubject: HttpResponse<ITeacherSubject>) => {
          if (teacherSubject.body) {
            return of(teacherSubject.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default teacherSubjectResolve;
