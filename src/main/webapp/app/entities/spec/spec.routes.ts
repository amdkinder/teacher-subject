import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SpecComponent } from './list/spec.component';
import { SpecDetailComponent } from './detail/spec-detail.component';
import { SpecUpdateComponent } from './update/spec-update.component';
import SpecResolve from './route/spec-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const specRoute: Routes = [
  {
    path: '',
    component: SpecComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SpecDetailComponent,
    resolve: {
      spec: SpecResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SpecUpdateComponent,
    resolve: {
      spec: SpecResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SpecUpdateComponent,
    resolve: {
      spec: SpecResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default specRoute;
