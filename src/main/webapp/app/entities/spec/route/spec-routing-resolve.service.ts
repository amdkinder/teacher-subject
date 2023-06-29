import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpec } from '../spec.model';
import { SpecService } from '../service/spec.service';

export const specResolve = (route: ActivatedRouteSnapshot): Observable<null | ISpec> => {
  const id = route.params['id'];
  if (id) {
    return inject(SpecService)
      .find(id)
      .pipe(
        mergeMap((spec: HttpResponse<ISpec>) => {
          if (spec.body) {
            return of(spec.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default specResolve;
