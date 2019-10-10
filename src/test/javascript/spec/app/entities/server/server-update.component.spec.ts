import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CloudappwatchTestModule } from '../../../test.module';
import { ServerUpdateComponent } from 'app/entities/server/server-update.component';
import { ServerService } from 'app/entities/server/server.service';
import { Server } from 'app/shared/model/server.model';

describe('Component Tests', () => {
  describe('Server Management Update Component', () => {
    let comp: ServerUpdateComponent;
    let fixture: ComponentFixture<ServerUpdateComponent>;
    let service: ServerService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CloudappwatchTestModule],
        declarations: [ServerUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ServerUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ServerUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ServerService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Server(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Server();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
