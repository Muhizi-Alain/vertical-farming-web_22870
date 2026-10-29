import { useEffect, useState } from 'react'
import { api } from '../api/client'
import Alert, { shouldShowAlert } from '../components/Alert'
import DataTable from '../components/DataTable'
import DetailPanel from '../components/DetailPanel'
import DeleteAllButton from '../components/DeleteAllButton'
import TableActions from '../components/TableActions'

export default function HarvestRecordsPage() {
  const [cycles, setCycles] = useState([])
  const [selectedId, setSelectedId] = useState('')
  const [history, setHistory] = useState([])
  const [result, setResult] = useState(null)
  const [detail, setDetail] = useState(null)
  const [form, setForm] = useState({ qualityGrade: '', notes: '', kilograms: '' })

  function loadCycles() {
    api('/api/planting-cycles/all').then((response) => {
      if (response.status === 200) {
        setCycles(response.body)
      }
    })
  }

  useEffect(loadCycles, [])

  const selected = cycles.find((cycle) => cycle.id === selectedId)
  const harvested = selected?.status === 'HARVESTED'

  async function create(event) {
    event.preventDefault()
    const response = await api(`/api/harvest-records/save?plantingCycleId=${selectedId}`, {
      method: 'POST',
      body: {
        qualityGrade: form.qualityGrade,
        notes: form.notes,
        kilograms: Number(form.kilograms),
      },
    })
    setResult(response)
    if (response.status === 201) {
      setForm({ qualityGrade: '', notes: '', kilograms: '' })
      if (selected?.cropBatch?.batchCode) {
        loadHistory(selected.cropBatch.batchCode, false)
      }
    }
  }

  async function loadHistory(batchCode, showAlert = true) {
    const response = await api(`/api/harvest-records/crop-batch/${encodeURIComponent(batchCode)}`)
    if (showAlert && shouldShowAlert(response)) {
      setResult(response)
    } else if (response.status === 200) {
      setHistory(response.body)
    }
  }

  async function search(event) {
    event.preventDefault()
    loadHistory(new FormData(event.currentTarget).get('batchCode'))
  }

  function view(record) {
    setDetail([
      ['Recorded date', record.recordedDate],
      ['Quality grade', record.qualityGrade],
      ['Notes', record.notes],
      ['Kilograms', `${record.kilograms} kg`],
      ['Batch', record.plantingCycle?.cropBatch?.batchCode],
      ['Grower', `${record.plantingCycle?.grower?.firstName || ''} ${record.plantingCycle?.grower?.lastName || ''}`.trim()],
    ])
  }

  async function removeAll() {
    const response = await api('/api/harvest-records/delete-all', { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      setHistory([])
      setDetail(null)
    }
  }

  async function remove(record) {
    if (!window.confirm('Delete this harvest record?')) {
      return
    }
    const response = await api(`/api/harvest-records/delete/${record.id}`, { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      const batchCode = record.plantingCycle?.cropBatch?.batchCode
      if (batchCode) {
        loadHistory(batchCode, false)
      }
    }
  }

  return (
    <section>
      <h2>Harvest records</h2>
      <Alert result={result} onDismiss={() => setResult(null)} />
      <DetailPanel title="Harvest record details" fields={detail} onClose={() => setDetail(null)} />
      <form className="form panel" onSubmit={create}>
        <div className="form-heading">
          <strong>Create harvest record</strong>
        </div>
        <label>
          Planting cycle
          <select value={selectedId} onChange={(event) => setSelectedId(event.target.value)} required>
            <option value="">Select a cycle</option>
            {cycles.map((cycle) => (
              <option key={cycle.id} value={cycle.id}>
                {cycle.plantingDate} {cycle.status} {cycle.cropBatch?.batchCode}
              </option>
            ))}
          </select>
        </label>
        <label>
          Quality grade
          <input value={form.qualityGrade} onChange={(event) => setForm({ ...form, qualityGrade: event.target.value })} disabled={!harvested} required={harvested} />
        </label>
        <label>
          Notes
          <input value={form.notes} onChange={(event) => setForm({ ...form, notes: event.target.value })} disabled={!harvested} />
        </label>
        <label>
          Kilograms
          <input type="number" min="0" value={form.kilograms} onChange={(event) => setForm({ ...form, kilograms: event.target.value })} disabled={!harvested} required={harvested} />
        </label>
        <button type="submit" disabled={!harvested}>
          Save harvest
        </button>
      </form>
      {selectedId && !harvested && (
        <p className="info-banner">Harvest records are only for HARVESTED planting cycles.</p>
      )}
      <form className="form panel" onSubmit={search}>
        <label>
          Batch code
          <input name="batchCode" defaultValue="LET-2026-07" required />
        </label>
        <button type="submit">Load history</button>
      </form>
      <DataTable
        title="Harvest history"
        subtitle={history.length > 0 ? `${history.length} records loaded` : 'Load by batch code to read records'}
        isEmpty={history.length === 0}
        emptyMessage="No harvest records to display yet."
        headerActions={<DeleteAllButton label="harvest records" onDelete={removeAll} />}
      >
        <thead>
          <tr>
            <th>Recorded</th>
            <th className="col-center">Grade</th>
            <th>Notes</th>
            <th className="col-num">Kilograms</th>
            <th>Batch</th>
            <th className="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          {history.map((record) => (
            <tr key={record.id}>
              <td className="cell-strong">{record.recordedDate}</td>
              <td className="col-center">{record.qualityGrade}</td>
              <td className="cell-muted">{record.notes || '—'}</td>
              <td className="col-num">{record.kilograms}</td>
              <td>{record.plantingCycle?.cropBatch?.batchCode}</td>
              <td className="col-actions">
                <TableActions showEdit={false} onView={() => view(record)} onDelete={() => remove(record)} />
              </td>
            </tr>
          ))}
        </tbody>
      </DataTable>
      <p className="muted">The API supports create, read, and delete for harvest records. Update is not part of the backend specification.</p>
    </section>
  )
}
