import { useEffect, useState } from 'react'
import { api } from '../api/client'
import Alert from '../components/Alert'
import DataTable from '../components/DataTable'
import DetailPanel from '../components/DetailPanel'
import DeleteAllButton from '../components/DeleteAllButton'
import TableActions from '../components/TableActions'

export default function GrowTowersPage() {
  const [rows, setRows] = useState([])
  const [free, setFree] = useState([])
  const [result, setResult] = useState(null)
  const [form, setForm] = useState({ towerNumber: '', name: '' })
  const [editId, setEditId] = useState(null)
  const [detail, setDetail] = useState(null)

  function load() {
    api('/api/grow-towers/all').then((response) => response.status === 200 && setRows(response.body))
    api('/api/grow-towers/free').then((response) => response.status === 200 && setFree(response.body))
  }

  useEffect(load, [])

  function resetForm() {
    setForm({ towerNumber: '', name: '' })
    setEditId(null)
  }

  async function submit(event) {
    event.preventDefault()
    const body = { towerNumber: Number(form.towerNumber), name: form.name }
    const response = editId
      ? await api(`/api/grow-towers/update/${editId}`, { method: 'PUT', body })
      : await api('/api/grow-towers/save', { method: 'POST', body })
    setResult(response)
    if (response.status === 200 || response.status === 201) {
      resetForm()
      load()
    }
  }

  async function view(id) {
    const response = await api(`/api/grow-towers/${id}`)
    if (response.status === 200) {
      setDetail([
        ['Tower number', response.body.towerNumber],
        ['Name', response.body.name],
        ['Id', response.body.id],
      ])
    } else {
      setResult(response)
    }
  }

  function edit(row) {
    setEditId(row.id)
    setForm({ towerNumber: String(row.towerNumber), name: row.name })
    setDetail(null)
  }

  async function removeAll() {
    const response = await api('/api/grow-towers/delete-all', { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      resetForm()
      load()
    }
  }

  async function remove(row) {
    if (!window.confirm(`Delete grow tower ${row.towerNumber}?`)) {
      return
    }
    const response = await api(`/api/grow-towers/delete/${row.id}`, { method: 'DELETE' })
    setResult(response)
    if (response.status === 200) {
      load()
      if (editId === row.id) {
        resetForm()
      }
    }
  }

  const sorted = rows.slice().sort((a, b) => a.towerNumber - b.towerNumber)

  return (
    <section>
      <h2>Grow towers</h2>
      <Alert result={result} onDismiss={() => setResult(null)} />
      <DetailPanel title="Grow tower details" fields={detail} onClose={() => setDetail(null)} />
      <form className="form panel" onSubmit={submit}>
        <div className="form-heading">
          <strong>{editId ? 'Update grow tower' : 'Create grow tower'}</strong>
          {editId && (
            <button type="button" className="small ghost" onClick={resetForm}>
              Cancel edit
            </button>
          )}
        </div>
        <label>
          Tower number
          <input type="number" value={form.towerNumber} onChange={(event) => setForm({ ...form, towerNumber: event.target.value })} required />
        </label>
        <label>
          Name
          <input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} required />
        </label>
        <button type="submit">{editId ? 'Update' : 'Save'}</button>
      </form>
      {free.length > 0 && (
        <p className="info-banner">
          Free towers: {free.map((tower) => `Tower ${tower.towerNumber} (${tower.name})`).join(', ')}
        </p>
      )}
      <DataTable
        title="Tower inventory"
        subtitle={`${sorted.length} towers`}
        isEmpty={sorted.length === 0}
        emptyMessage="No grow towers yet."
        headerActions={<DeleteAllButton label="grow towers" onDelete={removeAll} disabled={sorted.length === 0} />}
      >
        <thead>
          <tr>
            <th className="col-num">Number</th>
            <th>Name</th>
            <th className="col-center">Occupancy</th>
            <th className="col-actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          {sorted.map((tower) => {
            const isFree = free.some((item) => item.id === tower.id)
            return (
              <tr key={tower.id}>
                <td className="col-num cell-strong">{tower.towerNumber}</td>
                <td>{tower.name}</td>
                <td className="col-center">
                  <span className={`status-badge ${isFree ? 'status-planned' : 'status-growing'}`}>{isFree ? 'Free' : 'Occupied'}</span>
                </td>
                <td className="col-actions">
                  <TableActions onView={() => view(tower.id)} onEdit={() => edit(tower)} onDelete={() => remove(tower)} />
                </td>
              </tr>
            )
          })}
        </tbody>
      </DataTable>
    </section>
  )
}
