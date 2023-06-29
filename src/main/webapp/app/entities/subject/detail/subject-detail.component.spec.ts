import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubjectDetailComponent } from './subject-detail.component';

describe('Subject Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubjectDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SubjectDetailComponent,
              resolve: { subject: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(SubjectDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load subject on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SubjectDetailComponent);

      // THEN
      expect(instance.subject).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
