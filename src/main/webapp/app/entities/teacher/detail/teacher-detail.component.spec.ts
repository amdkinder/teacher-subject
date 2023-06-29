import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TeacherDetailComponent } from './teacher-detail.component';

describe('Teacher Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeacherDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TeacherDetailComponent,
              resolve: { teacher: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(TeacherDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load teacher on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TeacherDetailComponent);

      // THEN
      expect(instance.teacher).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
