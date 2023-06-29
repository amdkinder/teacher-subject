import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SpecDetailComponent } from './spec-detail.component';

describe('Spec Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpecDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SpecDetailComponent,
              resolve: { spec: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(SpecDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load spec on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SpecDetailComponent);

      // THEN
      expect(instance.spec).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
