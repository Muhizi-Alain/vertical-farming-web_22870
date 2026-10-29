import { useEffect, useState } from 'react'

const empty = {
  plantingDate: '',
  trays: '',
  cropBatchId: '',
  growerId: '',
}

export default function PlantingCycleForm({ batches, growers, editCycle, onSubmit, onCancelEdit }) {
  const [form, setForm] = useState(empty)

  useEffect(() => {
    if (editCycle) {
      setForm({
        plantingDate: editCycle.plantingDate || '',
        trays: String(editCycle.trays ?? ''),
        cropBatchId: editCycle.cropBatch?.id || '',
        growerId: editCycle.grower?.id || '',
      })
    } else {
      setForm(empty)
    }
  }, [editCycle])

  function change(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  function submit(event) {
    event.preventDefault()
    onSubmit({
      editId: editCycle?.id,
      body: {
        plantingDate: form.plantingDate,
        trays: Number(form.trays),
        cropBatch: { id: form.cropBatchId },
        grower: { id: form.growerId },
      },
    })
    if (!editCycle) {
      setForm(empty)
    }
  }

  return (
    <form className="form" onSubmit={submit}>
      <div className="form-heading">
        <strong>{editCycle ? 'Update planting cycle' : 'Create planting cycle'}</strong>
        {editCycle && (
          <button type="button" className="small ghost" onClick={onCancelEdit}>
            Cancel edit
          </button>
        )}
      </div>
      <label>
        Planting date
        <input type="date" value={form.plantingDate} onChange={(event) => change('plantingDate', event.target.value)} required />
      </label>
      <label>
        Trays
        <input type="number" min="1" value={form.trays} onChange={(event) => change('trays', event.target.value)} required />
      </label>
      <label>
        Crop batch
        <select value={form.cropBatchId} onChange={(event) => change('cropBatchId', event.target.value)} required>
          <option value="">Select batch</option>
          {batches.map((batch) => (
            <option key={batch.id} value={batch.id}>
              {batch.batchCode} ({batch.cropName})
            </option>
          ))}
        </select>
      </label>
      <label>
        Grower
        <select value={form.growerId} onChange={(event) => change('growerId', event.target.value)} required>
          <option value="">Select grower</option>
          {growers.map((grower) => (
            <option key={grower.id} value={grower.id}>
              {grower.firstName} {grower.lastName}
            </option>
          ))}
        </select>
      </label>
      <button type="submit">{editCycle ? 'Update cycle' : 'Save cycle'}</button>
    </form>
  )
}
