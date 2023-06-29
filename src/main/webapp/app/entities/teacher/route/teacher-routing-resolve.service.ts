import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeacher } from '../teacher.model';
import { TeacherService } from '../service/teacher.service';

export const teacherResolve = (route: ActivatedRouteSnapshot): Observable<null | ITeacher> => {
  const id = route.params['id'];
  if (id) {
    return inject(TeacherService)
      .find(id)
      .pipe(
        mergeMap((teacher: HttpResponse<ITeacher>) => {
          if (teacher.body) {
            return of(teacher.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default teacherResolve;
